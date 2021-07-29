package com.shopwise.admin.entity.services;

import com.shopwise.admin.entity.repositories.ProductRepository;
import com.shopwise.admin.entity.services.impl.IProductService;
import com.shopwise.admin.utils.ProductNotFoundException;
import com.shopwise.common.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class ProductService implements IProductService {

    public static final int PRODUCTS_PER_PAGE = 4;

    @Autowired
    private ProductRepository repository;

    @Override
    public Product getById(Integer id) throws ProductNotFoundException {
        try {
            return repository.findById(id).get();

        } catch (NoSuchElementException e) {
            throw new ProductNotFoundException("Could not find any product with ID: " + id);
        }
    }

    @Override
    public List<Product> getAll() {
        return (List<Product>) repository.findAll();
    }

    @Override
    public Product save(Product product) {
        if(product.getId() == null){
            product.setCreatedAt(new Date());
        }
        //when product is new
        if(product.getAlias() == null || product.getAlias().isEmpty()){
            String defaultAlias = product.getName().toLowerCase(Locale.ROOT).replaceAll(" ","_");
            product.setAlias(defaultAlias);
        }
        //when product is being updated
        else{
            product.setAlias(product.getAlias().toLowerCase(Locale.ROOT).replaceAll(" ","_"));
        }

        product.setUpdatedAt(new Date());

        return repository.save(product);
    }

    @Override
    public String checkUniqueness(Integer id, String name, String alias) {
        boolean isNew = (id == null || id == 0);

        Product byName = repository.findByName(name);

        if (isNew) {
            if (byName != null) return "Duplicate";
            else {
                Product byAlias = repository.findByAlias(alias);

                if (byAlias != null) return "Duplicate";
            }
        } else {
            if (byName != null && !byName.getId().equals(id)) return "Duplicate";

            Product byAlias = repository.findByAlias(alias);
            if (byAlias != null && !byAlias.getId().equals(id)) return "Duplicate";
        }

        return "OK";
    }

    @Override
    public void updateEnabledStatus(Integer id, boolean enabled) {
        repository.toggleEnabledStatus(id, enabled);
    }

    @Override
    public void delete(Integer id) throws ProductNotFoundException {
        Long countById = repository.countById(id);

        if(countById == null || countById == 0){
            throw new ProductNotFoundException("Could not find any product with ID: " + id);
        }

        repository.deleteById(id);
    }

    @Override
    public Page<Product> getByPage(int pageNumber, String sortField, String sortDirection, String keyword, Integer categoryId) {
        Sort sort = Sort.by(sortField);

        sort = sortDirection.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, PRODUCTS_PER_PAGE, sort);

        if (keyword != null && !keyword.isEmpty()) {

            if (categoryId != null && categoryId > 0) {
                String categoryIdMatch = "-" + categoryId + "-";
                return repository.searchInCategory(categoryId, categoryIdMatch, keyword, pageable);
            }
            return repository.findAll(keyword, pageable);
        }

        if (categoryId != null && categoryId > 0) {
            String categoryIdMatch = "-" + categoryId + "-";
            return repository.findAllInCategory(categoryId, categoryIdMatch, pageable);
        }
        return repository.findAll(pageable);
    }

    @Override
    public void saveProductPrice(Product productInForm) {
        Product productInDB = repository.findById(productInForm.getId()).get();
        productInDB.setCost(productInForm.getCost());
        productInDB.setPrice(productInForm.getPrice());
        productInDB.setDiscountPercentage(productInForm.getDiscountPercentage());

        repository.save(productInDB);
    }
}
