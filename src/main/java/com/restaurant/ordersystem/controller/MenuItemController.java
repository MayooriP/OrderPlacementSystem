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

    // GET all
    @GetMapping
    public ResponseEntity<List<MenuItem>> getAllMenuItems() {
        logger.info("Retrieving all menu items");
        List<MenuItem> menuItems = menuItemRepository.findAll();
        return new ResponseEntity<>(menuItems, HttpStatus.OK);
    }

    // GET by ID
    @GetMapping("/{itemId}")
    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable Integer itemId) {
        logger.info("Retrieving menu item with ID: {}", itemId);
        MenuItem menuItem = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", "id", itemId));
        return new ResponseEntity<>(menuItem, HttpStatus.OK);
    }

    // POST - Create
    @PostMapping
    public ResponseEntity<MenuItem> createMenuItem(@RequestBody MenuItem menuItem) {
        logger.info("Creating new menu item: {}", menuItem.getName());
        MenuItem savedItem = menuItemRepository.save(menuItem);
        return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
    }

    // PUT - Update
    @PutMapping("/{itemId}")
    public ResponseEntity<MenuItem> updateMenuItem(@PathVariable Integer itemId,
                                                   @RequestBody MenuItem updatedItem) {
        logger.info("Updating menu item with ID: {}", itemId);
        MenuItem existingItem = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", "id", itemId));

        existingItem.setName(updatedItem.getName());
        existingItem.setDescription(updatedItem.getDescription());
        existingItem.setCategory(updatedItem.getCategory());
        existingItem.setSubCategory(updatedItem.getSubCategory());
        existingItem.setPrice(updatedItem.getPrice());
        existingItem.setImageUrl(updatedItem.getImageUrl());
        existingItem.setAvailable(updatedItem.getAvailable());
        existingItem.setCreatedDateTime(updatedItem.getCreatedDateTime());
        existingItem.setCreatedBy(updatedItem.getCreatedBy());
        existingItem.setLastModifiedDateTime(updatedItem.getLastModifiedDateTime());
        existingItem.setLastModifiedBy(updatedItem.getLastModifiedBy());
        existingItem.setStatus(updatedItem.getStatus());

        MenuItem savedItem = menuItemRepository.save(existingItem);
        return new ResponseEntity<>(savedItem, HttpStatus.OK);
    }

    // DELETE
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Integer itemId) {
        logger.info("Deleting menu item with ID: {}", itemId);
        MenuItem existingItem = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", "id", itemId));
        menuItemRepository.delete(existingItem);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
