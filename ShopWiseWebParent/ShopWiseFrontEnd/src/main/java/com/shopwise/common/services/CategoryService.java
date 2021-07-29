package com.shopwise.common.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopwise.common.entity.Category;
import com.shopwise.common.entity.Product;
import com.shopwise.common.exception.CategoryNotFoundException;
import com.shopwise.common.repository.CategoryRepository;
import com.shopwise.common.services.impl.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService implements ICategoryService {

    @Autowired
    private CategoryRepository repository;

    @Override
    public Category get(Integer id) throws CategoryNotFoundException {
        Category category = repository.findById(id).get();

        if(category == null){
            throw new CategoryNotFoundException("Could not find any categories with id " + id);
        }

        return category;
    }

    @Override
    public Category get(String alias) throws CategoryNotFoundException{
        Category category = repository.findByAliasEnabled(alias);

        if(category == null){
            throw new CategoryNotFoundException("Could not find any categories with alias " + alias);
        }

        return category;
    }

    @Override
    public List<Category> getChildlessCategories() {
        List<Category> list = new ArrayList<>();
        for (Category category : repository.findAllEnabled()) {
            if (category.getChildren() == null || category.getChildren().size() == 0) {
                list.add(category);
            }
        }
        return list;
    }

    @Override
    public List<Category> getCategoryParents(Category child) {
        List<Category> listParents = new ArrayList<>();

        Category parent = child.getParent();

        while (parent != null) {
            listParents.add(0, parent);
            parent = parent.getParent();
        }

        listParents.add(child);

        return listParents;
    }
}
