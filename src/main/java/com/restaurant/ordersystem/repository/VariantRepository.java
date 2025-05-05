package com.restaurant.ordersystem.repository;

import com.restaurant.ordersystem.model.MenuItem;
import com.restaurant.ordersystem.model.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VariantRepository extends JpaRepository<Variant, Integer> {
    
    List<Variant> findByItem(MenuItem item);
}
