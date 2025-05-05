package com.restaurant.ordersystem.service;

import com.restaurant.ordersystem.exception.InvalidOrderException;
import com.restaurant.ordersystem.exception.ResourceNotFoundException;
import com.restaurant.ordersystem.model.Restaurant;
import com.restaurant.ordersystem.model.RestaurantWorkingHours;
import com.restaurant.ordersystem.repository.RestaurantRepository;
import com.restaurant.ordersystem.repository.RestaurantWorkingHoursRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
public class RestaurantService {
    
    private final RestaurantRepository restaurantRepository;
    private final RestaurantWorkingHoursRepository workingHoursRepository;
    
    @Autowired
    public RestaurantService(RestaurantRepository restaurantRepository, 
                             RestaurantWorkingHoursRepository workingHoursRepository) {
        this.restaurantRepository = restaurantRepository;
        this.workingHoursRepository = workingHoursRepository;
    }
    
    public Restaurant getRestaurantById(Integer restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", restaurantId));
    }
    
    public void validateRestaurantAvailability(Integer restaurantId, LocalDateTime deliveryDateTime) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        
        // Get day of week
        DayOfWeek dayOfWeek = deliveryDateTime.getDayOfWeek();
        String dayName = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        
        // Check if restaurant is closed on Monday
        if (dayOfWeek == DayOfWeek.MONDAY) {
            throw new InvalidOrderException("Restaurant is closed on Monday");
        }
        
        // Get delivery time
        LocalTime deliveryTime = deliveryDateTime.toLocalTime();
        
        // Check if delivery time is within working hours
        // Restaurant works Tue - Sun 11:00 AM - 2:00 PM and 5:00PM to 10:00PM
        boolean isLunchHours = isTimeBetween(deliveryTime, LocalTime.of(11, 0), LocalTime.of(14, 0));
        boolean isDinnerHours = isTimeBetween(deliveryTime, LocalTime.of(17, 0), LocalTime.of(22, 0));
        
        if (!isLunchHours && !isDinnerHours) {
            throw new InvalidOrderException("Restaurant is not available at the requested delivery time. " +
                    "Working hours are 11:00 AM - 2:00 PM and 5:00 PM - 10:00 PM");
        }
    }
    
    private boolean isTimeBetween(LocalTime time, LocalTime start, LocalTime end) {
        return !time.isBefore(start) && !time.isAfter(end);
    }
}
