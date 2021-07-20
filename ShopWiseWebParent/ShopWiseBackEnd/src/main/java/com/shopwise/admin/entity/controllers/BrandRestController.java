package com.shopwise.admin.entity.controllers;

import com.shopwise.admin.entity.services.BrandService;
import com.shopwise.admin.utils.BrandNotFoundException;
import com.shopwise.admin.utils.BrandNotFoundRestException;
import com.shopwise.common.entity.Brand;
import com.shopwise.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
public class BrandRestController {
    @Autowired
    private BrandService service;

    @PostMapping("/brands/check_uniqueness")
    public String checkUniqueness(@Param("id") Integer id, @Param("name") String name){
        return service.checkUniqueness(id, name);
    }

    @GetMapping("/brands/{id}/categories")
    public List<CategoryDTO> getCategoriesByBrand(@PathVariable(name = "id") Integer brandId) throws BrandNotFoundRestException {
        List<CategoryDTO> categoryDTOList = new ArrayList<>();
        try {
            Brand brand = service.getById(brandId);
            Set<Category> categories = brand.getCategories();

            for(Category category: categories){
                CategoryDTO dto = new CategoryDTO(category.getId(), category.getName());
                categoryDTOList.add(dto);
            }
            return categoryDTOList;
        } catch (BrandNotFoundException e) {
            throw new BrandNotFoundRestException();
        }
    }
}
