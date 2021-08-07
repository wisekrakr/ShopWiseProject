package com.shopwise.common.controller;

import com.shopwise.common.controller.utils.JsonConverter;
import com.shopwise.common.services.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/")
public record CategoryController(CategoryService service) {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    public CategoryController {
    }

    @GetMapping("/c/childless")
    public String getAllChildlessCategories() {
        LOGGER.info("CategoryController | getAllChildlessCategories is started ");
        return JsonConverter.convert(service.getChildlessCategories());
    }


//    @GetMapping("/c/{id}")
//    public String getCategoryById(@PathVariable(value = "id") Integer id){
//        LOGGER.info("CategoryController | getCategoryById is started");
//        Category category = null;
//        try {
//            category = service.get(id);
//        } catch (CategoryNotFoundException e) {
//            LOGGER.info("CategoryController | CategoryNotFoundException | getCategoryById: " + e.getMessage());
//        }
//        return JsonConverter.convert(category);
//    }
}
