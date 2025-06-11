package com.restaurant.ordersystem.service;

import com.restaurant.ordersystem.exception.ResourceNotFoundException;
import com.restaurant.ordersystem.model.Customer;
import com.restaurant.ordersystem.model.Payment;
import com.restaurant.ordersystem.model.PaymentMethod;
import com.restaurant.ordersystem.repository.CustomerRepository;
import com.restaurant.ordersystem.repository.PaymentRepository;

import software.amazon.awssdk.services.dynamodb.model.Get;

import com.restaurant.ordersystem.model.PaymentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;



@Service
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;

    public PaymentService(PaymentRepository paymentRepository, CustomerRepository customerRepository) {
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * Create a new payment
     *
     * @param customerId Customer ID
     * @param amount Payment amount
     * @param status Payment status (PAID or PENDING)
     * @return Payment ID
     */
    public String createPayment(Integer customerId, BigDecimal amount, String status) {
        logger.info("Creating payment for customer ID: {} with amount: {} and status: {}", customerId, amount, status);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        Payment payment = new Payment();
        payment.setPaymentId(UUID.randomUUID().toString());
        payment.setCustomer(customer);
        payment.setAmount(amount);

        // Set payment method based on status
        if ("PAID".equalsIgnoreCase(status)) {
        payment.setPaymentMethod(PaymentMethod.UPI);
        payment.setStatus(PaymentStatus.PAID);
        } else {
        payment.setPaymentMethod(PaymentMethod.CASH);
        payment.setStatus(PaymentStatus.PENDING);
        }


        payment.setPaymentDate(LocalDateTime.now());
        payment.setCreatedDateTime(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);
        logger.info("Payment created with ID: {}", savedPayment.getPaymentId());

        return savedPayment.getPaymentId();
    }

    /**
     * Get payment by ID
     *
     * @param paymentId Payment ID
     * @return Payment object
     */
    public Payment getPaymentById(String paymentId) {
        if (paymentId == null) {
            return null;
        }

        return paymentRepository.findById(paymentId)
                .orElse(null);
    }

    /**
     * Cancel a payment
     *
     * @param paymentId Payment ID
     */
  public void cancelPayment(String paymentId) {
    logger.info("Cancelling payment with ID: {}", paymentId);

    Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));

    payment.setStatus(PaymentStatus.CANCELLED); // ✅ Fix here
    payment.setLastModifiedDateTime(LocalDateTime.now());

    paymentRepository.save(payment);
    logger.info("Payment cancelled: {}", paymentId);
}


    /**
     * Complete a pending payment
     *
     * @param paymentId Payment ID
     */
    public void completePayment(String paymentId) {
    logger.info("Completing payment with ID: {}", paymentId);

    Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));

    if (payment.getStatus() == PaymentStatus.PENDING) {
        payment.setStatus(PaymentStatus.PAID);  // ✅ Correct usage
        payment.setLastModifiedDateTime(LocalDateTime.now());

        paymentRepository.save(payment);
        logger.info("Payment completed: {}", paymentId);
    } else {
        logger.warn("Cannot complete payment with ID: {} as it is not in PENDING status", paymentId);
    }
}

public void savePayment(Payment payment) {
    paymentRepository.save(payment);
}

public Payment getPaymentByOrderId(String orderId) {
    logger.info("Retrieving payment for order ID: {}", orderId);
    
    if (orderId == null) {
        return null;
    }
    
    Payment payment = paymentRepository.findByOrderId(orderId);
    
    if (payment != null) {
        logger.info("Found payment with ID: {} for order ID: {}", payment.getPaymentId(), orderId);
    } else {
        logger.warn("No payment found for order ID: {}", orderId);
    }
    
    return payment;
}

}

