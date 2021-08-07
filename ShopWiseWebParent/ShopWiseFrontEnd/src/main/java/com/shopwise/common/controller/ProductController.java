package com.shopwise.common.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.shopwise.common.controller.utils.JsonConverter;
import com.shopwise.common.entity.Brand;
import com.shopwise.common.entity.Category;
import com.shopwise.common.entity.Product;
import com.shopwise.common.exception.CategoryNotFoundException;
import com.shopwise.common.exception.ProductNotFoundException;
import com.shopwise.common.services.CategoryService;
import com.shopwise.common.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/")
public record ProductController(ProductService productService, CategoryService categoryService) {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    public ProductController {

    }

    @GetMapping("/p/{id}")
    public List<List<String>> getProductById(@PathVariable(value = "id") Integer id) throws ProductNotFoundException {
        LOGGER.info("ProductController | getProductById is started");
        List<List<String>> jsonStringArray = new ArrayList<>();

        Product product;
        try {
            product = productService.get(id);
        } catch (ProductNotFoundException e) {
            throw new ProductNotFoundException("Could not find any product " + e.getMessage());
        }

        List<String>jsonList = new ArrayList<>();
        jsonList.add(JsonConverter.convert(product));
        jsonList.add(JsonConverter.convert(categoryService.getCategoryParents(product.getCategory())));
        jsonList.add(JsonConverter.convert(product.getBrand()));
        jsonList.add(JsonConverter.convert(product.getDetails()));
        jsonList.add(JsonConverter.convert(product.getImages()));

        jsonStringArray.add(jsonList);

        return jsonStringArray;
    }

    @GetMapping("/c/{category_alias}")
    public List<List<String>> getProductsByCategoryFirstPage(@PathVariable("category_alias") String alias) {

        LOGGER.info("ProductController | getProductsByCategoryFirstPage is called with alias: " + alias);

        try {
            return getProductsByCategory(alias,1);
        } catch (JsonProcessingException e) {
            LOGGER.info("ProductController | JsonProcessingException: " + e.getMessage());
            throw new IllegalStateException("JsonProcessingException: " + e);
        }
    }


    @GetMapping("/c/{category_alias}/page/{pageNumber}")
    public List<List<String>> getProductsByCategory(@PathVariable(value = "category_alias") String alias ,@PathVariable(value = "pageNumber") int pageNumber) throws JsonProcessingException {
        LOGGER.info("ProductController | getCategoryProducts is started");
        // create an List of json strings, give them proper names so that the react front end can easily extract them.
        List<List<String>> jsonStringArray = new ArrayList<>();

        Category category = null;
        try {
            category = categoryService.get(alias);
        } catch (CategoryNotFoundException e) {
            LOGGER.info("ProductController | CategoryNotFoundException | getCategoryProducts: " + e.getMessage());
        }
        LOGGER.info("ProductController |  getCategoryProducts | Category: " + category);

        List<Category> categories = categoryService.getCategoryParents(category);
        Page<Product> pageProducts = productService.listByCategory(pageNumber, category.getId());

        LOGGER.info("ProductController |  getCategoryProducts | content size " + pageProducts.getContent().size());

        List<Product> products = pageProducts.getContent();

        long startCount = (long) (pageNumber - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
        long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;

        if (endCount > pageProducts.getTotalElements()) {
            endCount = pageProducts.getTotalElements();
        }

        List<String>jsonList = new ArrayList<>();
        jsonList.add(JsonConverter.convert(categories));
        jsonList.add(JsonConverter.convert(products));
        jsonList.add(JsonConverter.convert(pageNumber));
        jsonList.add(JsonConverter.convert(pageProducts.getTotalPages()));
        jsonList.add(JsonConverter.convert(startCount));
        jsonList.add(JsonConverter.convert(endCount));
        jsonList.add(JsonConverter.convert(pageProducts.getTotalElements()));
        jsonList.add(JsonConverter.convert(category));
        jsonList.add(JsonConverter.convert(category.getChildren()));

        jsonStringArray.add(jsonList);

        return jsonStringArray;
    }

    @GetMapping("/search/{keyword}")
    public List<List<String>>searchFirstPage(@PathVariable(value = "keyword") String keyword){
        LOGGER.info("ProductController | searchFirstPage is called with keyword: " + keyword);
        return search(1,keyword);
    }

    @GetMapping("/search/page/{pageNumber}/{keyword}")
    public List<List<String>> search(@PathVariable(value = "pageNumber") int pageNumber,@PathVariable(value = "keyword") String keyword){
        LOGGER.info("ProductController | search is started");

        List<List<String>> jsonStringArray = new ArrayList<>();

        Page<Product> pageProducts = productService.search(keyword, pageNumber);

        LOGGER.info("ProductController |  getCategoryProducts | content size " + pageProducts.getContent().size());

        List<Product> products = pageProducts.getContent();

        long startCount = (long) (pageNumber - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
        long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;

        if (endCount > pageProducts.getTotalElements()) {
            endCount = pageProducts.getTotalElements();
        }


        List<String>jsonList = new ArrayList<>();
        jsonList.add(JsonConverter.convert(keyword));
        jsonList.add(JsonConverter.convert(products));
        jsonList.add(JsonConverter.convert(pageNumber));
        jsonList.add(JsonConverter.convert(pageProducts.getTotalPages()));
        jsonList.add(JsonConverter.convert(startCount));
        jsonList.add(JsonConverter.convert(endCount));
        jsonList.add(JsonConverter.convert(pageProducts.getTotalElements()));


        jsonStringArray.add(jsonList);

        return jsonStringArray;
    }
}
