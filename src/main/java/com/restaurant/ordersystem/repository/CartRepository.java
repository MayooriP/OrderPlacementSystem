package com.restaurant.ordersystem.repository;

import com.restaurant.ordersystem.model.Cart;
import com.restaurant.ordersystem.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    
    Optional<Cart> findByCustomerAndStatus(Customer customer, String status);
}
