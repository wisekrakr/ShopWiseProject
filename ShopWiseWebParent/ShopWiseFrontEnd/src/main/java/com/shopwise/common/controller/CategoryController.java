package com.shopwise.common.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopwise.common.entity.Category;
import com.shopwise.common.exception.CategoryNotFoundException;
import com.shopwise.common.services.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/")
public record CategoryController(CategoryService service) {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    public CategoryController {
    }

    @GetMapping("/categories/childless")
    public String getAllChildlessCategories() {
        LOGGER.info("CategoryController | getAllChildlessCategories is started ");

        List<Category> categories = service.getChildlessCategories();

        ObjectMapper mapper = new ObjectMapper();

        String all = null;
        try {
            all = mapper.writeValueAsString(categories);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return all;
    }

    @GetMapping("/c/{category_alias}")
    public String getCategoryParents(@PathVariable(value = "category_alias") String alias) throws JsonProcessingException {
        LOGGER.info("CategoryController | getCategoryParents is started");

        Category category = null;
        try {
            category = service.get(alias);
        } catch (CategoryNotFoundException e) {
            LOGGER.info("CategoryController | CategoryNotFoundException | getCategoryParents: " + e.getMessage());
        }

        List<Category> categories = service.getCategoryParents(category);

        ObjectMapper mapper = new ObjectMapper();

        return mapper.writeValueAsString(categories);
    }

    @GetMapping("/categories/{id}")
    public String getCategoryById(@PathVariable(value = "id") Integer id) throws IOException {
        LOGGER.info("CategoryController | getCategoryById is started");
        Category category = null;
        try {
            category = service.get(id);
        } catch (CategoryNotFoundException e) {
            LOGGER.info("CategoryController | CategoryNotFoundException | getCategoryById: " + e.getMessage());
        }

        ObjectMapper mapper = new ObjectMapper();

        return mapper.writeValueAsString(category);
    }
}
