package com.restaurant.ordersystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class HomeController {

    @GetMapping("/")
    @ResponseBody
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Welcome to Restaurant Order System API");
        response.put("version", "1.0.0");
        response.put("apiDocs", "/api-docs");
        
        // Add available endpoints
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("Orders", "/api/orders");
        endpoints.put("Menu Items", "/api/menu-items");
        endpoints.put("Categories", "/api/categories");
        endpoints.put("Cart", "/api/cart");
        endpoints.put("Customers", "/api/customers");
        
        response.put("endpoints", endpoints);
        
        return response;
    }
}
