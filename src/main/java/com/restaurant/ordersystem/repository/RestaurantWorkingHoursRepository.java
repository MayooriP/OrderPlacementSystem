package com.restaurant.ordersystem.repository;

import com.restaurant.ordersystem.model.Restaurant;
import com.restaurant.ordersystem.model.RestaurantWorkingHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface RestaurantWorkingHoursRepository extends JpaRepository<RestaurantWorkingHours, Integer> {
    
    List<RestaurantWorkingHours> findByRestaurantAndDayOfTheWeek(Restaurant restaurant, String dayOfWeek);
    
    @Query("SELECT rwh FROM RestaurantWorkingHours rwh WHERE rwh.restaurant.restaurantId = :restaurantId " +
           "AND rwh.dayOfTheWeek = :dayOfWeek " +
           "AND :time BETWEEN rwh.startTime AND rwh.endTime")
    List<RestaurantWorkingHours> findByRestaurantIdAndDayAndTimeInRange(
            @Param("restaurantId") Integer restaurantId,
            @Param("dayOfWeek") String dayOfWeek,
            @Param("time") LocalTime time);
}
