package com.restaurant.ordersystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "referrals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Referral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer referralId;

    @Column(nullable = false, unique = true)
    private String referralCode;

    @ManyToOne
    @JoinColumn(name = "referrer_id", nullable = false)
    private Customer referrer;

    @ManyToOne
    @JoinColumn(name = "referred_id")
    private Customer referred;

    private Boolean isUsed;

    private LocalDateTime usedDate;

    private LocalDateTime createdDateTime;

    @Enumerated(EnumType.STRING)
    private ReferralStatus status;

    public enum ReferralStatus {
        Active, Used, Expired
    }

    // Manually added getters and setters
    public Integer getReferralId() {
        return referralId;
    }

    public void setReferralId(Integer referralId) {
        this.referralId = referralId;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public Customer getReferrer() {
        return referrer;
    }

    public void setReferrer(Customer referrer) {
        this.referrer = referrer;
    }

    public Customer getReferred() {
        return referred;
    }

    public void setReferred(Customer referred) {
        this.referred = referred;
    }

    public Boolean getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(Boolean isUsed) {
        this.isUsed = isUsed;
    }

    public LocalDateTime getUsedDate() {
        return usedDate;
    }

    public void setUsedDate(LocalDateTime usedDate) {
        this.usedDate = usedDate;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public ReferralStatus getStatus() {
        return status;
    }

    public void setStatus(ReferralStatus status) {
        this.status = status;
    }
}
