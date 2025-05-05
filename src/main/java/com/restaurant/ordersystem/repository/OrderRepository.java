package com.restaurant.ordersystem.repository;

import com.restaurant.ordersystem.model.Customer;
import com.restaurant.ordersystem.model.Order;
import com.restaurant.ordersystem.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    
    List<Order> findByCustomer(Customer customer);
    
    List<Order> findByRestaurant(Restaurant restaurant);
    
    List<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);
}
