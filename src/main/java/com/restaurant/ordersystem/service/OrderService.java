package com.restaurant.ordersystem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.ordersystem.dto.OrderItemDTO;
import com.restaurant.ordersystem.dto.OrderRequestDTO;
import com.restaurant.ordersystem.dto.OrderResponseDTO;
import com.restaurant.ordersystem.exception.InvalidCouponException;
import com.restaurant.ordersystem.exception.InvalidOrderException;
import com.restaurant.ordersystem.exception.ResourceNotFoundException;
import com.restaurant.ordersystem.model.*;
import com.restaurant.ordersystem.repository.*;
import com.restaurant.ordersystem.util.RestaurantHoursUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final VariantRepository variantRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CouponRepository couponRepository;
    private final VoucherRepository voucherRepository;
    private final ReferralRepository referralRepository;
    private final RestaurantWorkingHoursRepository restaurantWorkingHoursRepository;
    private final PaymentService paymentService;
    private final DiscountService discountService;
    private final DynamoDBService dynamoDBService;
    private final RestaurantHoursUtil restaurantHoursUtil;
    private final ObjectMapper objectMapper;

    public OrderService(CustomerRepository customerRepository,
                        RestaurantRepository restaurantRepository,
                        CartRepository cartRepository,
                        CartItemRepository cartItemRepository,
                        MenuItemRepository menuItemRepository,
                        VariantRepository variantRepository,
                        OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        CouponRepository couponRepository,
                        VoucherRepository voucherRepository,
                        ReferralRepository referralRepository,
                        RestaurantWorkingHoursRepository restaurantWorkingHoursRepository,
                        PaymentService paymentService,
                        DiscountService discountService,
                        DynamoDBService dynamoDBService,
                        RestaurantHoursUtil restaurantHoursUtil,
                        ObjectMapper objectMapper) {
        this.customerRepository = customerRepository;
        this.restaurantRepository = restaurantRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.menuItemRepository = menuItemRepository;
        this.variantRepository = variantRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.couponRepository = couponRepository;
        this.voucherRepository = voucherRepository;
        this.referralRepository = referralRepository;
        this.restaurantWorkingHoursRepository = restaurantWorkingHoursRepository;
        this.paymentService = paymentService;
        this.discountService = discountService;
        this.dynamoDBService = dynamoDBService;
        this.restaurantHoursUtil = restaurantHoursUtil;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public OrderResponseDTO placeOrder(OrderRequestDTO orderRequest) {
        // 1. Validate required fields
        validateOrderRequest(orderRequest);

        // 2. Get customer and restaurant
        Customer customer = customerRepository.findById(orderRequest.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", orderRequest.getCustomerId()));

        Restaurant restaurant = restaurantRepository.findById(orderRequest.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", orderRequest.getRestaurantId()));

        // 3. Check restaurant availability
        checkRestaurantAvailability(restaurant, orderRequest.getDeliveryDate());

        // 4. Get active cart for customer
        Cart cart = cartRepository.findByCustomerAndStatus(customer, "ACTIVE")
                .orElseThrow(() -> new InvalidOrderException("No active cart found for customer"));

        // 5. Check if cart has items
        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        if (cartItems.isEmpty()) {
            throw new InvalidOrderException("Cart is empty. Cannot place order with empty cart.");
        }

       // 6. Calculate prices and apply discounts
BigDecimal totalPrice = cart.getTotalAmount();
BigDecimal discountValue = BigDecimal.ZERO;
String appliedCouponCode = null;

// Apply coupon/voucher/referral if provided
String code = orderRequest.getCouponCode();
if (code != null && !code.isEmpty()) {
    try {
        // Try as Coupon
        Coupon coupon = discountService.validateCoupon(code);
        discountValue = discountService.applyCouponDiscount(coupon, totalPrice);
        appliedCouponCode = coupon.getCouponCode();
        logger.info("Coupon applied successfully: {}", code);
    } catch (InvalidCouponException e1) {
        logger.info("Not a valid coupon, trying as voucher: {}", e1.getMessage());

        try {
            // Try as Voucher
            Voucher voucher = discountService.validateVoucher(code);
            discountValue = discountService.applyVoucherDiscount(voucher, totalPrice);
            appliedCouponCode = voucher.getVoucherCode();
            logger.info("Voucher applied successfully: {}", code);
        } catch (InvalidCouponException e2) {
            logger.info("Not a valid voucher, trying as referral: {}", e2.getMessage());

            try {
                // Try as Referral
                Referral referral = discountService.validateReferral(code, customer);
                discountValue = discountService.applyReferralDiscount(referral, totalPrice);
                appliedCouponCode = referral.getReferralCode();
                discountService.markReferralAsUsed(referral);
                logger.info("Referral applied successfully: {}", code);
            } catch (Exception e3) {
                logger.warn("Invalid referral code: {} - {}", code, e3.getMessage());
            }
        }
    } catch (Exception ex) {
        logger.error("Error applying discount: {}", ex.getMessage());
    }
}

BigDecimal finalPrice = totalPrice.subtract(discountValue);


// 7. Create payment (only if online payment)
        String paymentId = null;

        if (orderRequest.getPaymentMethod().equalsIgnoreCase("UPI")) {
        paymentId = paymentService.createPayment(
        customer.getCustomerId(),
        finalPrice,
        PaymentStatus.PAID.name());  // using enum name to avoid typos
        }

        // 8. Create order
        Order order = new Order();
        order.setCustomer(customer);
        order.setRestaurant(restaurant);
        order.setPaymentId(paymentId);
        order.setOrderDate(orderRequest.getOrderDate());
        order.setDeliveryDate(orderRequest.getDeliveryDate());
        order.setStatus(Order.OrderStatus.Received);
        order.setPickupInstructions(orderRequest.getPickupInstructions());
        order.setLastModifiedDateTime(LocalDateTime.now());

        // Set coupon if applied
        if (appliedCouponCode != null) {
            Coupon coupon = couponRepository.findByCouponCode(appliedCouponCode).orElse(null);
            if (coupon != null) {
                order.setCoupon(coupon);
            }
        }

        // Create status history
        Map<String, Object> statusEntry = new HashMap<>();
        statusEntry.put("status", Order.OrderStatus.Received.name());
        statusEntry.put("timestamp", LocalDateTime.now().toString());
        statusEntry.put("notes", "Order received");

        try {
            order.setStatusHistory(objectMapper.writeValueAsString(Collections.singletonList(statusEntry)));
        } catch (JsonProcessingException e) {
            logger.error("Error creating status history: {}", e.getMessage());
            order.setStatusHistory("[]");
        }

        // Save order
        Order savedOrder = orderRepository.save(order);

        // 9. Create order items
        List<OrderItem> orderItems = new ArrayList<>();
        List<OrderItemDTO> orderItemDTOs = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            MenuItem menuItem = cartItem.getMenuItem();
            Variant variant = cartItem.getVariant();

            // Create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setMenuItem(menuItem);
            orderItem.setVariant(variant);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setSubtotal(cartItem.getSubtotal());
            orderItem.setSpecialInstructions(cartItem.getSpecialInstructions());
            orderItem.setIsFreeItem(false);

            // Set item name and category details
            orderItem.setItemName(menuItem.getName());

            if (menuItem.getCategory() != null) {
                orderItem.setCategoryName(menuItem.getCategory().getName());
            }

            if (menuItem.getSubCategory() != null) {
                orderItem.setSubCategoryName(menuItem.getSubCategory().getName());
            }

            // Set variant name if available
            if (variant != null) {
                orderItem.setVariantName(variant.getVariantName());
            }

            // Save order item
            OrderItem savedOrderItem = orderItemRepository.save(orderItem);
            orderItems.add(savedOrderItem);

            // Create DTO for response
            OrderItemDTO itemDTO = new OrderItemDTO();
            itemDTO.setOrderItemId(savedOrderItem.getOrderItemId());
            itemDTO.setMenuItemId(menuItem.getItemId());
            itemDTO.setMenuItemName(menuItem.getName());
            itemDTO.setQuantity(cartItem.getQuantity());
            itemDTO.setPrice(cartItem.getPrice());
            itemDTO.setSubtotal(cartItem.getSubtotal());
            itemDTO.setSpecialInstructions(cartItem.getSpecialInstructions());
            itemDTO.setIsFreeItem(false);

            if (menuItem.getCategory() != null) {
                itemDTO.setCategoryName(menuItem.getCategory().getName());
            }

            if (menuItem.getSubCategory() != null) {
                itemDTO.setSubCategoryName(menuItem.getSubCategory().getName());
            }

            if (variant != null) {
                itemDTO.setVariantId(variant.getVariantId());
                itemDTO.setVariantName(variant.getVariantName());
            }

            orderItemDTOs.add(itemDTO);
        }

        // 10. Clear the cart
        cartItems.forEach(cartItemRepository::delete);
        cart.setStatus("COMPLETED");
        cart.setLastModifiedDateTime(LocalDateTime.now());
        cartRepository.save(cart);

        // 11. Store order in DynamoDB
        try {
            dynamoDBService.saveOrder(savedOrder, customer, restaurant, orderItems);
        } catch (Exception e) {
            logger.error("Error saving order to DynamoDB: {}", e.getMessage());
        }

        // 12. Create response
        OrderResponseDTO responseDTO = new OrderResponseDTO();
        responseDTO.setOrderId(savedOrder.getOrderId());
        responseDTO.setCustomerId(customer.getCustomerId());
        responseDTO.setCustomerName(customer.getFullName());
        responseDTO.setRestaurantId(restaurant.getRestaurantId());
        responseDTO.setRestaurantName(restaurant.getName());
        responseDTO.setPaymentId(paymentId);
        responseDTO.setPaymentMethod(orderRequest.getPaymentMethod());
        responseDTO.setPaymentStatus(orderRequest.getPaymentMethod().equals("Pay Online") ? "Paid" : "Pending");
        responseDTO.setOrderDate(orderRequest.getOrderDate());
        responseDTO.setDeliveryDate(orderRequest.getDeliveryDate());
        responseDTO.setOrderStatus(Order.OrderStatus.Received.name());
        responseDTO.setTotalPrice(totalPrice);
        responseDTO.setDiscountValue(discountValue);
        responseDTO.setFinalPrice(finalPrice);
        responseDTO.setCouponCode(appliedCouponCode);
        responseDTO.setPickupInstructions(orderRequest.getPickupInstructions());
        responseDTO.setOrderItems(orderItemDTOs);
        responseDTO.setTotalItems(orderItemDTOs.stream().mapToInt(OrderItemDTO::getQuantity).sum());

        return responseDTO;
    }

    private void validateOrderRequest(OrderRequestDTO orderRequest) {
        if (orderRequest.getCustomerId() == null) {
            throw new InvalidOrderException("Customer ID is required");
        }

        if (orderRequest.getRestaurantId() == null) {
            throw new InvalidOrderException("Restaurant ID is required");
        }

        if (orderRequest.getPaymentMethod() == null || orderRequest.getPaymentMethod().isEmpty()) {
            throw new InvalidOrderException("Payment method is required");
        }

        if (!orderRequest.getPaymentMethod().equals("Cash") &&
            !orderRequest.getPaymentMethod().equals("UPI")) {
            throw new InvalidOrderException("Invalid payment method. Allowed values: 'Pay Online', 'Pay Cash'");
        }

        if (orderRequest.getOrderDate() == null) {
            throw new InvalidOrderException("Order date is required");
        }

        if (orderRequest.getDeliveryDate() == null) {
            throw new InvalidOrderException("Delivery date is required");
        }

        if (orderRequest.getDeliveryDate().isBefore(orderRequest.getOrderDate())) {
            throw new InvalidOrderException("Delivery date cannot be before order date");
        }
    }

    private void checkRestaurantAvailability(Restaurant restaurant, LocalDateTime deliveryDateTime) {
        // Use the RestaurantHoursUtil to check if the restaurant is open
        if (!restaurantHoursUtil.isRestaurantOpen(restaurant, deliveryDateTime)) {
            DayOfWeek dayOfWeek = deliveryDateTime.getDayOfWeek();
            String dayName = dayOfWeek.toString();

            // Get working hours for this day to provide a helpful error message
            List<RestaurantWorkingHours> workingHours = restaurantHoursUtil.getWorkingHoursForDay(restaurant, dayOfWeek);

            if (workingHours.isEmpty()) {
                // No specific hours defined for this day, check default hours
                List<RestaurantHoursUtil.TimeRange> defaultHours = restaurantHoursUtil.getDefaultWorkingHours(dayOfWeek);

                if (defaultHours.isEmpty()) {
                    // Restaurant is closed on this day
                    throw new InvalidOrderException("Restaurant is closed on " + dayName + "s");
                } else {
                    // Restaurant has default hours but the requested time is outside those hours
                    StringBuilder defaultTimesMessage = new StringBuilder("Restaurant is only open at the following times on " + dayName + ": ");
                    for (int i = 0; i < defaultHours.size(); i++) {
                        RestaurantHoursUtil.TimeRange range = defaultHours.get(i);
                        defaultTimesMessage.append(range.toString());
                        if (i < defaultHours.size() - 1) {
                            defaultTimesMessage.append(", ");
                        }
                    }
                    throw new InvalidOrderException(defaultTimesMessage.toString());
                }
            } else {
                // Restaurant has specific hours but the requested time is outside those hours
                StringBuilder availableHours = new StringBuilder("Restaurant is open at the following times on " + dayName + ": ");
                for (int i = 0; i < workingHours.size(); i++) {
                    RestaurantWorkingHours hours = workingHours.get(i);
                    availableHours.append(hours.getStartTime().toString()).append(" - ").append(hours.getEndTime().toString());
                    if (i < workingHours.size() - 1) {
                        availableHours.append(", ");
                    }
                }
                throw new InvalidOrderException("Restaurant is not open at the requested delivery time. " + availableHours.toString());
            }
        }
    }

    public OrderResponseDTO getOrderById(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        return convertToDTO(order);
    }

    public List<OrderResponseDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<OrderResponseDTO> getOrdersByCustomerId(Integer customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        List<Order> orders = orderRepository.findByCustomer(customer);
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<OrderResponseDTO> getOrdersByRestaurantId(Integer restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", restaurantId));

        List<Order> orders = orderRepository.findByRestaurant(restaurant);
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<OrderResponseDTO> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = orderRepository.findByOrderDateBetween(startDate, endDate);
        return orders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public OrderResponseDTO cancelOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        // Check if order can be cancelled
        if (order.getStatus() == Order.OrderStatus.OrderCompleted) {
            throw new InvalidOrderException("Cannot cancel a completed order");
        }

        // Update order status
        order.setStatus(Order.OrderStatus.Cancelled);
        order.setLastModifiedDateTime(LocalDateTime.now());

        // Update status history
        try {
            List<Map<String, Object>> statusHistory = objectMapper.readValue(
                    order.getStatusHistory(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class)
            );

            Map<String, Object> statusEntry = new HashMap<>();
            statusEntry.put("status", Order.OrderStatus.Cancelled.name());
            statusEntry.put("timestamp", LocalDateTime.now().toString());
            statusEntry.put("notes", "Order cancelled");

            statusHistory.add(statusEntry);
            order.setStatusHistory(objectMapper.writeValueAsString(statusHistory));
        } catch (Exception e) {
            logger.error("Error updating status history: {}", e.getMessage());
        }

        Order savedOrder = orderRepository.save(order);

        // Update payment status if needed
        if (savedOrder.getPaymentId() != null) {
            paymentService.cancelPayment(savedOrder.getPaymentId());
        }

        return convertToDTO(savedOrder);
    }

    private OrderResponseDTO convertToDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setOrderId(order.getOrderId());
        dto.setCustomerId(order.getCustomer().getCustomerId());
        dto.setCustomerName(order.getCustomer().getFullName());
        dto.setRestaurantId(order.getRestaurant().getRestaurantId());
        dto.setRestaurantName(order.getRestaurant().getName());
        dto.setPaymentId(order.getPaymentId());

        // Get payment details
        Payment payment = paymentService.getPaymentById(order.getPaymentId());
        if (payment != null) {
            dto.setPaymentMethod(payment.getPaymentMethod().name());
            dto.setPaymentStatus(payment.getStatus().name());
        }

        dto.setOrderDate(order.getOrderDate());
        dto.setDeliveryDate(order.getDeliveryDate());
        dto.setOrderStatus(order.getStatus().name());

        // Get order items
        List<OrderItem> orderItems = orderRepository.findOrderItemsByOrder(order);
        List<OrderItemDTO> orderItemDTOs = new ArrayList<>();

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderItem item : orderItems) {
            OrderItemDTO itemDTO = new OrderItemDTO();
            itemDTO.setOrderItemId(item.getOrderItemId());
            itemDTO.setMenuItemId(item.getMenuItem().getItemId());
            itemDTO.setMenuItemName(item.getMenuItem().getName());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setPrice(item.getPrice());
            itemDTO.setSubtotal(item.getSubtotal());
            itemDTO.setSpecialInstructions(item.getSpecialInstructions());

            if (item.getVariant() != null) {
                itemDTO.setVariantId(item.getVariant().getVariantId());
                itemDTO.setVariantName(item.getVariant().getVariantName());
            }

            orderItemDTOs.add(itemDTO);
            totalPrice = totalPrice.add(item.getSubtotal());
        }

        dto.setOrderItems(orderItemDTOs);
        dto.setTotalPrice(totalPrice);

        // Calculate discount
        BigDecimal discountValue = BigDecimal.ZERO;
        if (order.getCoupon() != null) {
            dto.setCouponCode(order.getCoupon().getCouponCode());
            discountValue = discountService.calculateDiscount(order.getCoupon(), totalPrice);
        }

        dto.setDiscountValue(discountValue);
        dto.setFinalPrice(totalPrice.subtract(discountValue));
        dto.setPickupInstructions(order.getPickupInstructions());

        return dto;
    }

    public OrderResponseDTO updatePaymentStatus(String orderId, String newStatus) {
    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

    if (order.getPaymentId() == null) {
        throw new InvalidOrderException("No payment associated with this order");
    }

    Payment payment = paymentService.getPaymentById(order.getPaymentId());

    if (payment == null) {
        throw new ResourceNotFoundException("Payment", "id", order.getPaymentId());
    }

    try {
        PaymentStatus statusEnum = PaymentStatus.valueOf(newStatus.toUpperCase());
        payment.setStatus(statusEnum);
        paymentService.savePayment(payment); 
    } catch (IllegalArgumentException e) {
        throw new InvalidOrderException("Invalid payment status: " + newStatus);
    }

    return convertToDTO(order); // updated DTO reflects new payment status
}

}





