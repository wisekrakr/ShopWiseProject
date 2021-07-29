package com.shopwise.common.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopwise.common.entity.Category;
import com.shopwise.common.entity.Product;
import com.shopwise.common.exception.CategoryNotFoundException;
import com.shopwise.common.exception.ProductNotFoundException;
import com.shopwise.common.services.CategoryService;
import com.shopwise.common.services.impl.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/")
public record ProductController(ProductService productService, CategoryService categoryService) {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    public ProductController {

    }

    @GetMapping("/products/{id}")
    public String getProductById(@PathVariable(value = "id") Integer id) throws IOException, ProductNotFoundException {
        LOGGER.info("ProductController | getProductById is started");
        Product product = null;
        try {
            product = productService.get(id);
        } catch (ProductNotFoundException e) {
            throw new ProductNotFoundException("Could not find any product " + e.getMessage());
        }

        ObjectMapper mapper = new ObjectMapper();

        return mapper.writeValueAsString(product);
    }

    @GetMapping("/c/{category_alias}")
    public String getCategoryParents(@PathVariable(value = "category_alias") String alias) throws JsonProcessingException {
        LOGGER.info("CategoryController | getCategoryParents is started");

        Category category = null;
        try {
            category = categoryService.get(alias);
        } catch (CategoryNotFoundException e) {
            LOGGER.info("CategoryController | CategoryNotFoundException | getCategoryParents: " + e.getMessage());
        }

        List<Category> categories = categoryService.getCategoryParents(category);

        ObjectMapper mapper = new ObjectMapper();

        return mapper.writeValueAsString(categories);
    }

}
