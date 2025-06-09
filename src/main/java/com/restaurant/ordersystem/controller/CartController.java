package com.restaurant.ordersystem.controller;

import com.restaurant.ordersystem.dto.CartDTO;
import com.restaurant.ordersystem.dto.CartItemDTO;
import com.restaurant.ordersystem.model.Cart;
import com.restaurant.ordersystem.model.CartItem;
import com.restaurant.ordersystem.model.Customer;
import java.util.List;
import com.restaurant.ordersystem.model.MenuItem;
import com.restaurant.ordersystem.repository.CartItemRepository;
import com.restaurant.ordersystem.repository.CartRepository;
import com.restaurant.ordersystem.repository.CustomerRepository;
import com.restaurant.ordersystem.repository.MenuItemRepository;
import com.restaurant.ordersystem.service.CartService;
import com.restaurant.ordersystem.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    private final CartService cartService;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final MenuItemRepository menuItemRepository;

    @Autowired
    public CartController(CartService cartService, CartRepository cartRepository,
                         CartItemRepository cartItemRepository, CustomerRepository customerRepository,
                         MenuItemRepository menuItemRepository) {
        this.cartService = cartService;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.customerRepository = customerRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<CartDTO> getCartByCustomerId(@PathVariable Integer customerId) {
        logger.info("Retrieving cart for customer ID: {}", customerId);
        CartDTO cartDTO = cartService.getCartDTO(customerId);
        logger.info("Retrieved cart for customer ID: {}", customerId);
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }

    @PostMapping("/{customerId}")
    public ResponseEntity<CartDTO> addOrUpdateCart(
            @PathVariable Integer customerId,
            @RequestBody List<CartItemDTO> cartItems) {
        CartDTO updatedCart = cartService.addOrUpdateCartItems(customerId, cartItems);
        return ResponseEntity.ok(updatedCart);
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addItemToCart(@RequestBody Map<String, Object> request) {
        Integer customerId = (Integer) request.get("customerId");
        Integer menuItemId = (Integer) request.get("menuItemId");
        Integer quantity = (Integer) request.get("quantity");
        String specialInstructions = (String) request.get("specialInstructions");

        logger.info("Adding item to cart for customer ID: {}", customerId);

        // Get customer
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        // Get menu item
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", "id", menuItemId));

        // Get or create cart
        Cart cart = cartRepository.findByCustomerAndStatus(customer, "ACTIVE")
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setCustomer(customer);
                    newCart.setTotalAmount(BigDecimal.ZERO);
                    newCart.setCreatedDateTime(LocalDateTime.now());
                    newCart.setLastModifiedDateTime(LocalDateTime.now());
                    newCart.setStatus("ACTIVE");
                    return cartRepository.save(newCart);
                });

        // Create cart item
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setMenuItem(menuItem);
        cartItem.setQuantity(quantity);
        cartItem.setPrice(menuItem.getPrice());
        cartItem.setSubtotal(menuItem.getPrice().multiply(BigDecimal.valueOf(quantity)));
        cartItem.setSpecialInstructions(specialInstructions);

        cartItemRepository.save(cartItem);

        // Update cart total
        BigDecimal newTotal = cart.getTotalAmount().add(cartItem.getSubtotal());
        cart.setTotalAmount(newTotal);
        cart.setLastModifiedDateTime(LocalDateTime.now());
        cartRepository.save(cart);

        logger.info("Item added to cart for customer ID: {}", customerId);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Item added to cart");
        response.put("cartId", cart.getCartId());
        response.put("cartItemId", cartItem.getCartItemId());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/item/{cartItemId}")
    public ResponseEntity<Map<String, Object>> updateCartItem(@PathVariable Integer cartItemId,
                                                            @RequestBody Map<String, Object> request) {
        Integer quantity = (Integer) request.get("quantity");
        String specialInstructions = (String) request.get("specialInstructions");

        logger.info("Updating cart item with ID: {}", cartItemId);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        // Calculate old subtotal
        BigDecimal oldSubtotal = cartItem.getSubtotal();

        // Update cart item
        cartItem.setQuantity(quantity);
        cartItem.setSubtotal(cartItem.getPrice().multiply(BigDecimal.valueOf(quantity)));
        cartItem.setSpecialInstructions(specialInstructions);

        cartItemRepository.save(cartItem);

        // Update cart total
        Cart cart = cartItem.getCart();
        BigDecimal newTotal = cart.getTotalAmount().subtract(oldSubtotal).add(cartItem.getSubtotal());
        cart.setTotalAmount(newTotal);
        cart.setLastModifiedDateTime(LocalDateTime.now());
        cartRepository.save(cart);

        logger.info("Updated cart item with ID: {}", cartItemId);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Cart item updated");
        response.put("cartItemId", cartItem.getCartItemId());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<Map<String, Object>> removeCartItem(@PathVariable Integer cartItemId) {
        logger.info("Removing cart item with ID: {}", cartItemId);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        // Update cart total
        Cart cart = cartItem.getCart();
        BigDecimal newTotal = cart.getTotalAmount().subtract(cartItem.getSubtotal());
        cart.setTotalAmount(newTotal);
        cart.setLastModifiedDateTime(LocalDateTime.now());

        // Remove cart item
        cartItemRepository.delete(cartItem);
        cartRepository.save(cart);

        logger.info("Removed cart item with ID: {}", cartItemId);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Cart item removed");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

   @DeleteMapping("/customer/{customerId}")
    public ResponseEntity<Map<String, Object>> clearCart(@PathVariable Integer customerId) {
    logger.info("Clearing cart for customer ID: {}", customerId);

    Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

    Cart cart = cartRepository.findByCustomerAndStatus(customer, "ACTIVE")
            .orElseThrow(() -> new ResourceNotFoundException("Active cart not found for customer with id: " + customerId));

    // Clear cart items
    List<CartItem> cartItems = cart.getCartItems();
    if (!cartItems.isEmpty()) {
        cartItemRepository.deleteAll(cartItems); // Deletes from DB
        cartItems.clear(); // Clears in memory
    }

    cart.setTotalAmount(BigDecimal.ZERO);
    cart.setLastModifiedDateTime(LocalDateTime.now());
    cartRepository.save(cart);

    logger.info("Cleared cart for customer ID: {}", customerId);

    Map<String, Object> response = new HashMap<>();
    response.put("status", "success");
    response.put("message", "Cart cleared");

    return new ResponseEntity<>(response, HttpStatus.OK);
}

}
