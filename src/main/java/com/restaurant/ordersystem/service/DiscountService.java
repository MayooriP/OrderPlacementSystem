package com.restaurant.ordersystem.service;

import com.restaurant.ordersystem.exception.InvalidCouponException;
import com.restaurant.ordersystem.model.*;
import com.restaurant.ordersystem.repository.CouponRepository;
import com.restaurant.ordersystem.repository.ReferralRepository;
import com.restaurant.ordersystem.repository.VoucherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DiscountService {
    private static final Logger logger = LoggerFactory.getLogger(DiscountService.class);

    private final CouponRepository couponRepository;
    private final VoucherRepository voucherRepository;
    private final ReferralRepository referralRepository;

    public DiscountService(CouponRepository couponRepository,
                          VoucherRepository voucherRepository,
                          ReferralRepository referralRepository) {
        this.couponRepository = couponRepository;
        this.voucherRepository = voucherRepository;
        this.referralRepository = referralRepository;
    }

    /**
     * Apply coupon discount to the order total
     */
    public BigDecimal applyCouponDiscount(Coupon coupon, BigDecimal totalPrice) {
        if (coupon == null) {
            return BigDecimal.ZERO;
        }

        // Validate coupon status
        if (coupon.getStatus() != Coupon.CouponStatus.Active) {
            logger.warn("Coupon {} is not active", coupon.getCouponCode());
            return BigDecimal.ZERO;
        }

        // Validate coupon dates
        LocalDateTime now = LocalDateTime.now();
        if (coupon.getStartDate() != null && now.isBefore(coupon.getStartDate())) {
            logger.warn("Coupon {} is not yet valid", coupon.getCouponCode());
            return BigDecimal.ZERO;
        }

        if (coupon.getEndDate() != null && now.isAfter(coupon.getEndDate())) {
            logger.warn("Coupon {} has expired", coupon.getCouponCode());
            return BigDecimal.ZERO;
        }

        return calculateDiscount(coupon, totalPrice);
    }

    /**
     * Apply voucher discount to the order total
     */
    public BigDecimal applyVoucherDiscount(Voucher voucher, BigDecimal totalPrice) {
        if (voucher == null) {
            return BigDecimal.ZERO;
        }

        // Validate voucher status
        if (voucher.getStatus() != Voucher.VoucherStatus.Active) {
            logger.warn("Voucher {} is not active", voucher.getVoucherCode());
            return BigDecimal.ZERO;
        }

        // Validate voucher expiry
        LocalDateTime now = LocalDateTime.now();
        if (voucher.getExpiryDate() != null && now.isAfter(voucher.getExpiryDate())) {
            logger.warn("Voucher {} has expired", voucher.getVoucherCode());
            return BigDecimal.ZERO;
        }

        // Check if voucher is already used
        if (voucher.getIsUsed()) {
            logger.warn("Voucher {} has already been used", voucher.getVoucherCode());
            return BigDecimal.ZERO;
        }

        // Mark voucher as used
        markVoucherAsUsed(voucher);

        return calculateVoucherDiscount(voucher, totalPrice);
    }

    /**
     * Apply referral discount to the order total
     */
    public BigDecimal applyReferralDiscount(Referral referral, BigDecimal totalPrice) {
        if (referral == null) {
            return BigDecimal.ZERO;
        }

        // Check if referral is already used
        if (referral.getIsUsed()) {
            logger.warn("Referral {} has already been used", referral.getReferralCode());
            return BigDecimal.ZERO;
        }

        // Apply 10% discount for referrals
        return totalPrice.multiply(BigDecimal.valueOf(0.1))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate discount for a coupon or voucher
     */
    public BigDecimal calculateDiscount(Coupon coupon, BigDecimal totalPrice) {
        if (coupon == null) {
            return BigDecimal.ZERO;
        }

        // Check minimum order value
        if (totalPrice.compareTo(BigDecimal.valueOf(coupon.getMinOrderValue())) < 0) {
            logger.warn("Order total does not meet the minimum amount required for coupon {}", coupon.getCouponCode());
            return BigDecimal.ZERO;
        }

        BigDecimal discountAmount;

        if ("PERCENTAGE".equalsIgnoreCase(coupon.getDiscountType())) {
            discountAmount = totalPrice.multiply(BigDecimal.valueOf(coupon.getCouponDiscountPercentage()))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            // Apply max discount cap if applicable
            BigDecimal maxAmount = BigDecimal.valueOf(coupon.getMaxAmount());
            if (discountAmount.compareTo(maxAmount) > 0) {
                discountAmount = maxAmount;
            }
        } else if ("FIXED".equalsIgnoreCase(coupon.getDiscountType())) {
            discountAmount = BigDecimal.valueOf(coupon.getMaxAmount());

            // Ensure discount doesn't exceed order total
            if (discountAmount.compareTo(totalPrice) > 0) {
                discountAmount = totalPrice;
            }
        } else {
            discountAmount = BigDecimal.ZERO;
        }

        return discountAmount;
    }

    public Coupon validateCoupon(String couponCode) {
        if (couponCode == null || couponCode.isEmpty()) {
            return null;
        }

        LocalDateTime now = LocalDateTime.now();

        // Check if it's a standard coupon
        Optional<Coupon> couponOpt = couponRepository.findByCouponCodeAndStatusAndStartDateBeforeAndEndDateAfter(
                couponCode, Coupon.CouponStatus.Active, now, now);

        if (couponOpt.isPresent()) {
            return couponOpt.get();
        }

        // Check if it's a voucher
        Optional<Voucher> voucherOpt = voucherRepository.findByVoucherCodeAndStatusAndExpiryDateAfterAndIsUsed(
                couponCode, Voucher.VoucherStatus.Active, now, false);

        if (voucherOpt.isPresent()) {
            throw new InvalidCouponException("Please use voucher code through the voucher section");
        }

        // Check if it's a referral code
        Optional<Referral> referralOpt = referralRepository.findByReferralCodeAndIsUsed(couponCode, false);

        if (referralOpt.isPresent()) {
            throw new InvalidCouponException("Please use referral code through the referral section");
        }

        throw new InvalidCouponException("Invalid or expired coupon code: " + couponCode);
    }

    public Voucher validateVoucher(String voucherCode) {
        if (voucherCode == null || voucherCode.isEmpty()) {
            return null;
        }

        LocalDateTime now = LocalDateTime.now();

        Optional<Voucher> voucherOpt = voucherRepository.findByVoucherCodeAndStatusAndExpiryDateAfterAndIsUsed(
                voucherCode, Voucher.VoucherStatus.Active, now, false);

        if (voucherOpt.isPresent()) {
            return voucherOpt.get();
        }

        throw new InvalidCouponException("Invalid or expired voucher code: " + voucherCode);
    }

    public Referral validateReferral(String referralCode, Customer customer) {
        if (referralCode == null || referralCode.isEmpty()) {
            return null;
        }

        Optional<Referral> referralOpt = referralRepository.findByReferralCodeAndIsUsed(referralCode, false);

        if (referralOpt.isPresent()) {
            Referral referral = referralOpt.get();

            // Check if the customer's phone number matches the referral
            if (customer.getEncryptedPhoneNumber() != null &&
                referral.getReferrer().getEncryptedPhoneNumber() != null &&
                !customer.getEncryptedPhoneNumber().equals(referral.getReferrer().getEncryptedPhoneNumber())) {
                return referral;
            } else {
                throw new InvalidCouponException("Referral code cannot be used by the referrer");
            }
        }

        throw new InvalidCouponException("Invalid or already used referral code: " + referralCode);
    }

    public BigDecimal calculateVoucherDiscount(Voucher voucher, BigDecimal totalPrice) {
        if (voucher == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal discountAmount;

        if (voucher.getDiscountPercentage() != null) {
            discountAmount = totalPrice.multiply(BigDecimal.valueOf(voucher.getDiscountPercentage()))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else if (voucher.getDiscountAmount() != null) {
            discountAmount = voucher.getDiscountAmount();

            // Ensure discount doesn't exceed order total
            if (discountAmount.compareTo(totalPrice) > 0) {
                discountAmount = totalPrice;
            }
        } else {
            discountAmount = BigDecimal.ZERO;
        }

        return discountAmount;
    }

    public BigDecimal calculateReferralDiscount(Referral referral, BigDecimal totalPrice) {
        if (referral == null) {
            return BigDecimal.ZERO;
        }

        // Apply 10% discount for referrals
        return totalPrice.multiply(BigDecimal.valueOf(0.1))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public void markVoucherAsUsed(Voucher voucher) {
        if (voucher != null) {
            voucher.setIsUsed(true);
            voucher.setUsedDate(LocalDateTime.now());
            voucher.setStatus(Voucher.VoucherStatus.Used);
            voucherRepository.save(voucher);
        }
    }

    public void markReferralAsUsed(Referral referral) {
        if (referral != null) {
            referral.setIsUsed(true);
            referral.setUsedDate(LocalDateTime.now());
            referral.setStatus(Referral.ReferralStatus.Used);
            referralRepository.save(referral);
        }
    }
}
