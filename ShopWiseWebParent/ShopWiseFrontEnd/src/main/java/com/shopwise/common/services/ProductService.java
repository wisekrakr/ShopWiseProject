package com.shopwise.common.services;

import com.shopwise.common.entity.Product;
import com.shopwise.common.exception.CategoryNotFoundException;
import com.shopwise.common.exception.ProductNotFoundException;
import com.shopwise.common.repository.ProductRepository;
import com.shopwise.common.services.impl.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService implements IProductService {
    public static final int PRODUCTS_PER_PAGE = 10;

    @Autowired
    private ProductRepository repository;

    @Override
    public Product get(Integer id)throws ProductNotFoundException {
        Product product = repository.findById(id).get();

        if(product == null){
            throw new ProductNotFoundException("Could not find any product with id " + id);
        }

        return product;
    }

    @Override
    public Product get(String alias) throws ProductNotFoundException {
        Product product = repository.findByAlias(alias);

        if(product == null){
            throw new ProductNotFoundException("Could not find any product with alias " + alias);
        }
        return product;
    }

    @Override
    public Page<Product> listByCategory(int pageNumber, Integer categoryId) {

        String categoryIdMatch = "-" + categoryId + "-";
        Pageable pageable = PageRequest.of(pageNumber - 1, PRODUCTS_PER_PAGE);

        return repository.listProductsByCategory(categoryId, categoryIdMatch, pageable);
    }



    @Override
    public Page<Product> search(String keyword, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber - 1, PRODUCTS_PER_PAGE);
        return repository.search(keyword, pageable);
    }

}
