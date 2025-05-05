package com.restaurant.ordersystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer customerId;
    
    private Integer userId;
    
    private String fullName;
    
    private String email;
    
    private LocalDateTime createdDateTime;
    
    private String createdBy;
    
    private LocalDateTime lastModifiedDateTime;
    
    private String lastModifiedBy;
    
    private String status;
    
    private String firstName;
    
    @Temporal(TemporalType.DATE)
    private Date dob;
    
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    private String encryptedPhoneNumber;
    
    private String encryptedEmail;
    
    public enum Gender {
        Male, Female, PREFER_NOT_TO_SAY
    }
}
