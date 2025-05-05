package com.restaurant.ordersystem.service;

import com.restaurant.ordersystem.model.Customer;
import com.restaurant.ordersystem.model.Payment;
import com.restaurant.ordersystem.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    
    @Autowired
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }
    
    public Payment createPayment(Customer customer, BigDecimal amount, String paymentMethod) {
        Payment payment = new Payment();
        payment.setCustomer(customer);
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentDate(LocalDateTime.now());
        
        // Set payment status based on payment method
        if ("Pay Online".equalsIgnoreCase(paymentMethod)) {
            payment.setStatus(Payment.PaymentStatus.Paid);
        } else if ("Pay at Restaurant".equalsIgnoreCase(paymentMethod)) {
            payment.setStatus(Payment.PaymentStatus.Pending);
        } else {
            payment.setStatus(Payment.PaymentStatus.Pending);
        }
        
        return paymentRepository.save(payment);
    }
}
