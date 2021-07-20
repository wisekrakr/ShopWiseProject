package com.shopwise.admin.entity.services.impl;

import com.shopwise.admin.utils.BrandNotFoundException;
import com.shopwise.common.entity.Brand;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IBrandService {
    List<Brand> getAll();
    Brand save(Brand brand);
    Brand update(Brand brand);
    Brand getById(Integer id) throws BrandNotFoundException;
    Brand getByName(String name);
    void delete(Integer id) throws BrandNotFoundException;
    String checkUniqueness(Integer id, String name);
    Page<Brand> getByPage(int pageNumber, String sortField, String sortDirection, String keyword);
}
