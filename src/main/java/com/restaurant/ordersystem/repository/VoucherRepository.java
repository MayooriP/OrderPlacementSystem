package com.restaurant.ordersystem.repository;

import com.restaurant.ordersystem.model.Customer;
import com.restaurant.ordersystem.model.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Integer> {
    
    Optional<Voucher> findByVoucherCode(String voucherCode);
    
    List<Voucher> findByCustomer(Customer customer);
    
    Optional<Voucher> findByVoucherCodeAndStatusAndExpiryDateAfterAndIsUsed(
            String voucherCode, 
            Voucher.VoucherStatus status, 
            LocalDateTime currentDate, 
            Boolean isUsed);
}
