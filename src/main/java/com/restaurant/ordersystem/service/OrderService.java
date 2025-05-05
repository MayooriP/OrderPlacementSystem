package com.restaurant.ordersystem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.ordersystem.dto.OrderItemDTO;
import com.restaurant.ordersystem.dto.OrderRequestDTO;
import com.restaurant.ordersystem.dto.OrderResponseDTO;
import com.restaurant.ordersystem.exception.InvalidOrderException;
import com.restaurant.ordersystem.exception.JsonProcessingException;
import com.restaurant.ordersystem.exception.ResourceNotFoundException;
import com.restaurant.ordersystem.model.*;
import com.restaurant.ordersystem.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final RestaurantService restaurantService;
    private final CartService cartService;
    private final DiscountService discountService;
    private final PaymentService paymentService;
    private final DynamoDBService dynamoDBService;
    private final ObjectMapper objectMapper;

    public OrderService(OrderRepository orderRepository,
                       CustomerService customerService,
                       RestaurantService restaurantService,
                       CartService cartService,
                       DiscountService discountService,
                       PaymentService paymentService,
                       DynamoDBService dynamoDBService,
                       ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.customerService = customerService;
        this.restaurantService = restaurantService;
        this.cartService = cartService;
        this.discountService = discountService;
        this.paymentService = paymentService;
        this.dynamoDBService = dynamoDBService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public OrderResponseDTO placeOrder(OrderRequestDTO orderRequest) {
        // 1. Validate required fields
        validateOrderRequest(orderRequest);

        // 2. Get customer and restaurant
        Customer customer = customerService.getCustomerById(orderRequest.getCustomerId());
        Restaurant restaurant = restaurantService.getRestaurantById(orderRequest.getRestaurantId());

        // 3. Validate restaurant availability
        restaurantService.validateRestaurantAvailability(orderRequest.getRestaurantId(), orderRequest.getDeliveryDate());

        // 4. Validate cart has items
        cartService.validateCartHasItems(orderRequest.getCustomerId());
        Cart cart = cartService.getActiveCartByCustomerId(orderRequest.getCustomerId());

        // 5. Validate and apply discount
        Coupon coupon = null;
        BigDecimal discountAmount = BigDecimal.ZERO;

        if (orderRequest.getCouponCode() != null && !orderRequest.getCouponCode().isEmpty()) {
            coupon = discountService.validateCoupon(orderRequest.getCouponCode());
            discountAmount = discountService.calculateCouponDiscount(coupon, cart.getTotalAmount());
        }

        // 6. Calculate final price
        BigDecimal totalPrice = cart.getTotalAmount();
        BigDecimal finalPrice = totalPrice.subtract(discountAmount);

        // 7. Create payment
        Payment payment = paymentService.createPayment(customer, finalPrice, orderRequest.getPaymentMethod());

        // 8. Create order
        Order order = new Order();
        order.setCustomer(customer);
        order.setRestaurant(restaurant);
        order.setPaymentId(payment.getPaymentId());
        order.setOrderDate(orderRequest.getOrderDate());
        order.setDeliveryDate(orderRequest.getDeliveryDate());
        order.setStatus(Order.OrderStatus.Received);
        order.setCoupon(coupon);
        order.setPickupInstructions(orderRequest.getPickupInstructions());
        order.setLastModifiedDateTime(LocalDateTime.now());

        // 9. Create status history
        Map<String, Object> statusEntry = new HashMap<>();
        statusEntry.put("status", Order.OrderStatus.Received.name());
        statusEntry.put("timestamp", LocalDateTime.now().toString());

        try {
            order.setStatusHistory(objectMapper.writeValueAsString(List.of(statusEntry)));
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new JsonProcessingException("Error creating status history", e);
        }

        // 10. Create order items from cart items
        List<OrderItem> orderItems = createOrderItemsFromCart(cart, order);
        order.setOrderItems(orderItems);

        // 11. Save order
        Order savedOrder = orderRepository.save(order);

        // 12. Create response DTO
        OrderResponseDTO responseDTO = createOrderResponseDTO(savedOrder, totalPrice, discountAmount, finalPrice);

        // 13. Save to DynamoDB
        dynamoDBService.saveOrderToDynamoDB(responseDTO);

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

        if (!orderRequest.getPaymentMethod().equals("Pay Online") && !orderRequest.getPaymentMethod().equals("Pay at Restaurant")) {
            throw new InvalidOrderException("Invalid payment method. Allowed values: 'Pay Online' or 'Pay at Restaurant'");
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

    private List<OrderItem> createOrderItemsFromCart(Cart cart, Order order) {
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMenuItem(cartItem.getMenuItem());
            orderItem.setVariant(cartItem.getVariant());
            orderItem.setItemName(cartItem.getMenuItem().getName());
            orderItem.setCategoryName(cartItem.getMenuItem().getCategory() != null ?
                    cartItem.getMenuItem().getCategory().getName() : null);
            orderItem.setSubCategoryName(cartItem.getMenuItem().getSubCategory() != null ?
                    cartItem.getMenuItem().getSubCategory().getName() : null);
            orderItem.setVariantName(cartItem.getVariant() != null ?
                    cartItem.getVariant().getVariantName() : null);
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setSubtotal(cartItem.getSubtotal());
            orderItem.setSpecialInstructions(cartItem.getSpecialInstructions());
            orderItem.setIsFreeItem(false);

            orderItems.add(orderItem);
        }

        return orderItems;
    }

    public List<OrderResponseDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(this::convertToOrderResponseDTO)
                .collect(Collectors.toList());
    }

    public OrderResponseDTO getOrderById(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        return convertToOrderResponseDTO(order);
    }

    public List<OrderResponseDTO> getOrdersByCustomerId(Integer customerId) {
        Customer customer = customerService.getCustomerById(customerId);
        List<Order> orders = orderRepository.findByCustomer(customer);
        return orders.stream()
                .map(this::convertToOrderResponseDTO)
                .collect(Collectors.toList());
    }

    public List<OrderResponseDTO> getOrdersByRestaurantId(Integer restaurantId) {
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
        List<Order> orders = orderRepository.findByRestaurant(restaurant);
        return orders.stream()
                .map(this::convertToOrderResponseDTO)
                .collect(Collectors.toList());
    }

    public List<OrderResponseDTO> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = orderRepository.findByOrderDateBetween(startDate, endDate);
        return orders.stream()
                .map(this::convertToOrderResponseDTO)
                .collect(Collectors.toList());
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
            // Parse existing status history
            List<Map<String, Object>> statusHistory = objectMapper.readValue(
                    order.getStatusHistory(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class));

            // Add new status entry
            Map<String, Object> statusEntry = new HashMap<>();
            statusEntry.put("status", Order.OrderStatus.Cancelled.name());
            statusEntry.put("timestamp", LocalDateTime.now().toString());
            statusHistory.add(statusEntry);

            // Update status history in order
            order.setStatusHistory(objectMapper.writeValueAsString(statusHistory));
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new JsonProcessingException("Error updating status history", e);
        }

        // Save updated order
        Order updatedOrder = orderRepository.save(order);

        // Return updated order response
        return convertToOrderResponseDTO(updatedOrder);
    }

    private OrderResponseDTO convertToOrderResponseDTO(Order order) {
        // Calculate total price from order items
        BigDecimal totalPrice = order.getOrderItems().stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate discount value (if coupon exists)
        BigDecimal discountValue = BigDecimal.ZERO;
        if (order.getCoupon() != null) {
            discountValue = discountService.calculateCouponDiscount(order.getCoupon(), totalPrice);
        }

        // Calculate final price
        BigDecimal finalPrice = totalPrice.subtract(discountValue);

        return createOrderResponseDTO(order, totalPrice, discountValue, finalPrice);
    }

    private OrderResponseDTO createOrderResponseDTO(Order order, BigDecimal totalPrice,
                                                   BigDecimal discountAmount, BigDecimal finalPrice) {
        OrderResponseDTO responseDTO = new OrderResponseDTO();
        responseDTO.setOrderId(order.getOrderId());
        responseDTO.setCustomerId(order.getCustomer().getCustomerId());
        responseDTO.setCustomerName(order.getCustomer().getFullName());
        responseDTO.setRestaurantId(order.getRestaurant().getRestaurantId());
        responseDTO.setRestaurantName(order.getRestaurant().getName());
        responseDTO.setPaymentId(order.getPaymentId());
        responseDTO.setPaymentMethod(order.getPaymentId() != null ?
                order.getPaymentId() : "Not Available");
        responseDTO.setPaymentStatus("Pending");
        responseDTO.setOrderDate(order.getOrderDate());
        responseDTO.setDeliveryDate(order.getDeliveryDate());
        responseDTO.setOrderStatus(order.getStatus().name());
        responseDTO.setTotalPrice(totalPrice);
        responseDTO.setDiscountValue(discountAmount);
        responseDTO.setFinalPrice(finalPrice);

        if (order.getCoupon() != null) {
            responseDTO.setCouponCode(order.getCoupon().getCouponCode());
        }

        responseDTO.setPickupInstructions(order.getPickupInstructions());

        List<OrderItemDTO> orderItemDTOs = order.getOrderItems().stream()
                .map(this::convertToOrderItemDTO)
                .collect(Collectors.toList());

        responseDTO.setOrderItems(orderItemDTOs);

        return responseDTO;
    }

    private OrderItemDTO convertToOrderItemDTO(OrderItem orderItem) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setItemId(orderItem.getMenuItem() != null ? orderItem.getMenuItem().getItemId() : null);
        dto.setItemName(orderItem.getItemName());
        dto.setVariantId(orderItem.getVariant() != null ? orderItem.getVariant().getVariantId() : null);
        dto.setVariantName(orderItem.getVariantName());
        dto.setCategoryName(orderItem.getCategoryName());
        dto.setSubCategoryName(orderItem.getSubCategoryName());
        dto.setPrice(orderItem.getPrice());
        dto.setQuantity(orderItem.getQuantity());
        dto.setSubtotal(orderItem.getSubtotal());
        dto.setSpecialInstructions(orderItem.getSpecialInstructions());
        dto.setIsFreeItem(orderItem.getIsFreeItem());

        return dto;
    }
}
