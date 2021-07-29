package com.shopwise.common.services.impl;

import com.shopwise.common.entity.Product;
import com.shopwise.common.exception.ProductNotFoundException;
import org.springframework.data.domain.Page;


public interface IProductService {

     /**
      * Get a product by id
      * @param id Integer
      * @return product
      */
     Product get(Integer id) throws ProductNotFoundException;

     Page<Product> listByCategory(int pageNum, Integer categoryId);

     Product get(String alias) throws ProductNotFoundException;

     Page<Product> search(String keyword, int pageNum);
}
