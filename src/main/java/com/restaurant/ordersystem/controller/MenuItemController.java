package com.restaurant.ordersystem.controller;

import com.restaurant.ordersystem.model.MenuItem;
import com.restaurant.ordersystem.repository.MenuItemRepository;
import com.restaurant.ordersystem.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu-items")
public class MenuItemController {

    private static final Logger logger = LoggerFactory.getLogger(MenuItemController.class);

    private final MenuItemRepository menuItemRepository;

    @Autowired
    public MenuItemController(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @GetMapping
    public ResponseEntity<List<MenuItem>> getAllMenuItems() {
        logger.info("Retrieving all menu items");
        List<MenuItem> menuItems = menuItemRepository.findAll();
        logger.info("Retrieved {} menu items", menuItems.size());
        return new ResponseEntity<>(menuItems, HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable Integer itemId) {
        logger.info("Retrieving menu item with ID: {}", itemId);
        MenuItem menuItem = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", "id", itemId));
        logger.info("Retrieved menu item with ID: {}", itemId);
        return new ResponseEntity<>(menuItem, HttpStatus.OK);
    }
}
