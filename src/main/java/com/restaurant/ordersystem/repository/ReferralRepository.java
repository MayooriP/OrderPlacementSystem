package com.restaurant.ordersystem.repository;

import com.restaurant.ordersystem.model.Customer;
import com.restaurant.ordersystem.model.Referral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReferralRepository extends JpaRepository<Referral, Integer> {
    
    Optional<Referral> findByReferralCode(String referralCode);
    
    List<Referral> findByReferrer(Customer referrer);
    
    Optional<Referral> findByReferralCodeAndIsUsed(String referralCode, Boolean isUsed);
}
