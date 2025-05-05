package com.restaurant.ordersystem.repository;

import com.restaurant.ordersystem.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Integer> {
    
    Optional<Coupon> findByCouponCode(String couponCode);
    
    Optional<Coupon> findByCouponCodeAndStatusAndStartDateBeforeAndEndDateAfter(
            String couponCode, 
            Coupon.CouponStatus status, 
            LocalDateTime currentDate, 
            LocalDateTime currentDate2);
}
