package com.shopwise.admin.entity.controllers;

import com.shopwise.admin.entity.services.CategoryService;
import com.shopwise.admin.entity.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductRestController {

    @Autowired
    private ProductService service;

    @PostMapping("/products/check_uniqueness")
    public String checkUniqueness(@Param("id") Integer id, @Param("name") String name, @Param("alias") String alias){
        return service.checkUniqueness(id, name, alias);
    }
}
