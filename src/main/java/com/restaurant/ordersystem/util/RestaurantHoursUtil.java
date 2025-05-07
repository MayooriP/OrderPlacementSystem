package com.restaurant.ordersystem.util;

import com.restaurant.ordersystem.model.Restaurant;
import com.restaurant.ordersystem.model.RestaurantWorkingHours;
import com.restaurant.ordersystem.repository.RestaurantWorkingHoursRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for managing restaurant working hours
 */
@Component
public class RestaurantHoursUtil {
    private static final Logger logger = LoggerFactory.getLogger(RestaurantHoursUtil.class);
    
    private final RestaurantWorkingHoursRepository restaurantWorkingHoursRepository;
    
    // Default working hours for days when no specific hours are defined
    private static final Map<DayOfWeek, List<TimeRange>> DEFAULT_WORKING_HOURS = new HashMap<>();
    
    static {
        // Restaurant works Tue - Sun 11:00 AM - 2:00 PM and 5:00 PM - 10:00 PM
        List<TimeRange> regularHours = List.of(
            new TimeRange(LocalTime.of(11, 0), LocalTime.of(14, 0)),
            new TimeRange(LocalTime.of(17, 0), LocalTime.of(22, 0))
        );
        
        // Set default hours for Tuesday through Sunday
        DEFAULT_WORKING_HOURS.put(DayOfWeek.TUESDAY, regularHours);
        DEFAULT_WORKING_HOURS.put(DayOfWeek.WEDNESDAY, regularHours);
        DEFAULT_WORKING_HOURS.put(DayOfWeek.THURSDAY, regularHours);
        DEFAULT_WORKING_HOURS.put(DayOfWeek.FRIDAY, regularHours);
        DEFAULT_WORKING_HOURS.put(DayOfWeek.SATURDAY, regularHours);
        DEFAULT_WORKING_HOURS.put(DayOfWeek.SUNDAY, regularHours);
        
        // Monday is closed by default (empty list)
        DEFAULT_WORKING_HOURS.put(DayOfWeek.MONDAY, List.of());
    }
    
    public RestaurantHoursUtil(RestaurantWorkingHoursRepository restaurantWorkingHoursRepository) {
        this.restaurantWorkingHoursRepository = restaurantWorkingHoursRepository;
    }
    
    /**
     * Check if a restaurant is open at the specified date and time
     * 
     * @param restaurant The restaurant to check
     * @param dateTime The date and time to check
     * @return true if the restaurant is open, false otherwise
     */
    public boolean isRestaurantOpen(Restaurant restaurant, LocalDateTime dateTime) {
        DayOfWeek dayOfWeek = dateTime.getDayOfWeek();
        LocalTime time = dateTime.toLocalTime();
        String dayName = dayOfWeek.toString();
        
        // First, check if there are specific working hours for this day
        List<RestaurantWorkingHours> workingHours = restaurantWorkingHoursRepository
                .findByRestaurantAndDayOfTheWeek(restaurant, dayName);
        
        if (!workingHours.isEmpty()) {
            // Check if the time is within any of the defined working hours
            for (RestaurantWorkingHours hours : workingHours) {
                if (!time.isBefore(hours.getStartTime()) && !time.isAfter(hours.getEndTime())) {
                    return true;
                }
            }
            return false;
        }
        
        // If no specific hours found, check if there are any hours where the time is within range
        List<RestaurantWorkingHours> hoursInRange = restaurantWorkingHoursRepository
                .findByRestaurantIdAndDayAndTimeInRange(restaurant.getRestaurantId(), dayName, time);
        
        if (!hoursInRange.isEmpty()) {
            return true;
        }
        
        // If still no working hours found, check default hours
        List<TimeRange> defaultHours = DEFAULT_WORKING_HOURS.get(dayOfWeek);
        if (defaultHours == null || defaultHours.isEmpty()) {
            return false; // Restaurant is closed on this day by default
        }
        
        // Check if the time is within any of the default working hours
        for (TimeRange range : defaultHours) {
            if (!time.isBefore(range.getStart()) && !time.isAfter(range.getEnd())) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Get the working hours for a restaurant on a specific day
     * 
     * @param restaurant The restaurant
     * @param dayOfWeek The day of the week
     * @return A list of working hours for the day
     */
    public List<RestaurantWorkingHours> getWorkingHoursForDay(Restaurant restaurant, DayOfWeek dayOfWeek) {
        String dayName = dayOfWeek.toString();
        List<RestaurantWorkingHours> workingHours = restaurantWorkingHoursRepository
                .findByRestaurantAndDayOfTheWeek(restaurant, dayName);
        
        return workingHours;
    }
    
    /**
     * Get the default working hours for a day
     * 
     * @param dayOfWeek The day of the week
     * @return A list of default time ranges for the day
     */
    public List<TimeRange> getDefaultWorkingHours(DayOfWeek dayOfWeek) {
        return DEFAULT_WORKING_HOURS.getOrDefault(dayOfWeek, List.of());
    }
    
    /**
     * Time range class for representing a range of time
     */
    public static class TimeRange {
        private final LocalTime start;
        private final LocalTime end;
        
        public TimeRange(LocalTime start, LocalTime end) {
            this.start = start;
            this.end = end;
        }
        
        public LocalTime getStart() {
            return start;
        }
        
        public LocalTime getEnd() {
            return end;
        }
        
        @Override
        public String toString() {
            return start + " - " + end;
        }
    }
}
