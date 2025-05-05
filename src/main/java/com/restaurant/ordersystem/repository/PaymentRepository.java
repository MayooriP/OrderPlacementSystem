package com.restaurant.ordersystem.repository;

import com.restaurant.ordersystem.model.Customer;
import com.restaurant.ordersystem.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    
    List<Payment> findByCustomer(Customer customer);
}
