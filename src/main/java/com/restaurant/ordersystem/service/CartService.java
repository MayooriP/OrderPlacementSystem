package com.restaurant.ordersystem.service;

import com.restaurant.ordersystem.dto.CartDTO;
import com.restaurant.ordersystem.dto.CartItemDTO;
import com.restaurant.ordersystem.exception.InvalidOrderException;
import com.restaurant.ordersystem.exception.ResourceNotFoundException;
import com.restaurant.ordersystem.model.Cart;
import com.restaurant.ordersystem.model.CartItem;
import com.restaurant.ordersystem.model.Customer;
import com.restaurant.ordersystem.repository.CartRepository;
import com.restaurant.ordersystem.repository.MenuItemRepository;
import com.restaurant.ordersystem.repository.VariantRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {
    
    private final CartRepository cartRepository;
    private final CustomerService customerService;
    private final MenuItemRepository menuItemRepository;
    private final VariantRepository variantRepository;

    
    @Autowired
    public CartService(CartRepository cartRepository, CustomerService customerService, MenuItemRepository menuItemRepository, VariantRepository variantRepository) {
        this.cartRepository = cartRepository;
        this.customerService = customerService;
        this.menuItemRepository = menuItemRepository;
        this.variantRepository = variantRepository;
    }
    
    public Cart getActiveCartByCustomerId(Integer customerId) {
        Customer customer = customerService.getCustomerById(customerId);
        return cartRepository.findByCustomerAndStatus(customer, "ACTIVE")
                .orElseThrow(() -> new ResourceNotFoundException("Active cart not found for customer with id: " + customerId));
    }
    
    public void validateCartHasItems(Integer customerId) {
        Cart cart = getActiveCartByCustomerId(customerId);
        
        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new InvalidOrderException("Cart is empty. Please add items to cart before placing an order.");
        }
    }

    public CartDTO addOrUpdateCartItems(Integer customerId, List<CartItemDTO> cartItemDTOs) {
    Customer customer = customerService.getCustomerById(customerId);

    Cart cart = cartRepository.findByCustomerAndStatus(customer, "ACTIVE")
            .orElseGet(() -> {
                Cart newCart = new Cart();
                newCart.setCustomer(customer);
                newCart.setStatus("ACTIVE");
                newCart.setTotalAmount(BigDecimal.ZERO);
                newCart.setCartItems(new ArrayList<>());
                newCart.setCreatedDateTime(LocalDateTime.now());
                return newCart;
            });

    cart.getCartItems().clear();

    BigDecimal totalAmount = BigDecimal.ZERO;
    for (CartItemDTO itemDTO : cartItemDTOs) {
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        menuItemRepository.findById(itemDTO.getMenuItemId()).ifPresent(cartItem::setMenuItem);
        if (itemDTO.getVariantId() != null) {
            variantRepository.findById(itemDTO.getVariantId()).ifPresent(cartItem::setVariant);
        } else {
            cartItem.setVariant(null);
        }
        cartItem.setQuantity(itemDTO.getQuantity());
        cartItem.setPrice(itemDTO.getPrice());
        cartItem.setSubtotal(itemDTO.getSubtotal());
        cartItem.setSpecialInstructions(itemDTO.getSpecialInstructions());

        cart.getCartItems().add(cartItem);
        if (itemDTO.getSubtotal() != null) {
            totalAmount = totalAmount.add(itemDTO.getSubtotal());
        }
    }

    cart.setTotalAmount(totalAmount);
    cart.setLastModifiedDateTime(LocalDateTime.now());

    cartRepository.save(cart);

    return getCartDTO(customerId);
}

    
    public CartDTO getCartDTO(Integer customerId) {
        Cart cart = getActiveCartByCustomerId(customerId);
        
        List<CartItemDTO> cartItemDTOs = cart.getCartItems().stream()
                .map(this::convertToCartItemDTO)
                .collect(Collectors.toList());
        
        CartDTO cartDTO = new CartDTO();
        cartDTO.setCartId(cart.getCartId());
        cartDTO.setCustomerId(cart.getCustomer().getCustomerId());
        cartDTO.setCartItems(cartItemDTOs);
        cartDTO.setTotalAmount(cart.getTotalAmount());
        cartDTO.setStatus(cart.getStatus());
        
        return cartDTO;
    }
    
    private CartItemDTO convertToCartItemDTO(CartItem cartItem) {
        CartItemDTO dto = new CartItemDTO();
        dto.setCartItemId(cartItem.getCartItemId());
        dto.setMenuItemId(cartItem.getMenuItem().getItemId());
        dto.setMenuItemName(cartItem.getMenuItem().getName());
        
        if (cartItem.getVariant() != null) {
            dto.setVariantId(cartItem.getVariant().getVariantId());
            dto.setVariantName(cartItem.getVariant().getVariantName());
        }
        
        dto.setQuantity(cartItem.getQuantity());
        dto.setPrice(cartItem.getPrice());
        dto.setSubtotal(cartItem.getSubtotal());
        dto.setSpecialInstructions(cartItem.getSpecialInstructions());
        
        return dto;
    }
}
