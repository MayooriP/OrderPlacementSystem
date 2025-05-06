package com.restaurant.ordersystem.repository;

import com.restaurant.ordersystem.model.Cart;
import com.restaurant.ordersystem.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    List<CartItem> findByCart(Cart cart);
}
