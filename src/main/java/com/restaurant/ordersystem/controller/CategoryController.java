package com.restaurant.ordersystem.controller;

import com.restaurant.ordersystem.model.Category;
import com.restaurant.ordersystem.repository.CategoryRepository;
import com.restaurant.ordersystem.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // GET all categories
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        logger.info("Retrieving all categories");
        List<Category> categories = categoryRepository.findAll();
        logger.info("Retrieved {} categories", categories.size());
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    // GET category by ID
    @GetMapping("/{categoryId}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Integer categoryId) {
        logger.info("Retrieving category with ID: {}", categoryId);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    // POST - Create new category
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        logger.info("Creating new category: {}", category.getName());
        Category savedCategory = categoryRepository.save(category);
        return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
    }

    // PUT - Update existing category
    @PutMapping("/{categoryId}")
    public ResponseEntity<Category> updateCategory(@PathVariable Integer categoryId,
                                                   @RequestBody Category updatedCategoryData) {
        logger.info("Updating category with ID: {}", categoryId);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        category.setName(updatedCategoryData.getName());
        category.setDescription(updatedCategoryData.getDescription());
        category.setStatus(updatedCategoryData.getStatus());
        category.setLastModifiedBy(updatedCategoryData.getLastModifiedBy());
        category.setLastModifiedDateTime(updatedCategoryData.getLastModifiedDateTime());

        Category updatedCategory = categoryRepository.save(category);
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    // DELETE - Remove category
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer categoryId) {
        logger.info("Deleting category with ID: {}", categoryId);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        categoryRepository.delete(category);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
