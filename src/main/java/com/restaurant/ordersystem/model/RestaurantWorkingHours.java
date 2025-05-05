package com.restaurant.ordersystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "restaurant_working_hours")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantWorkingHours {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer restaurantWorkingId;
    
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
    
    private String dayOfTheWeek;
    
    @Column(nullable = false)
    private LocalTime startTime;
    
    @Column(nullable = false)
    private LocalTime endTime;
    
    private LocalDateTime createdDateTime;
    
    private String createdBy;
    
    private LocalDateTime lastModifiedDateTime;
    
    private String lastModifiedBy;
    
    private String status;
    
    private LocalDate date;
}
