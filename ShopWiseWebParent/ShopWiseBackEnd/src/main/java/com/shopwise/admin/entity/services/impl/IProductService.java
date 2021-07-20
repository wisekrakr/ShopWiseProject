package com.shopwise.admin.entity.services.impl;

import com.shopwise.admin.utils.ProductNotFoundException;
import com.shopwise.common.entity.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IProductService {

     List<Product> getAll();

     Product save(Product product);

     String checkUniqueness(Integer id, String name, String alias);

     void updateEnabledStatus(Integer id, boolean enabled);

     void delete(Integer id) throws ProductNotFoundException;

     Product getById(Integer id) throws ProductNotFoundException;

     Page<Product> getByPage(int pageNumber, String sortField, String sortDirection,
                              String keyword, Integer categoryId);

     void saveProductPrice(Product productInForm);
}
