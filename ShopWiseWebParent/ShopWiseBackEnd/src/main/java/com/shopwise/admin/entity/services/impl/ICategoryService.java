package com.shopwise.admin.entity.services.impl;

import com.shopwise.admin.utils.CategoryNotFoundException;
import com.shopwise.admin.utils.CategoryPageInfo;
import com.shopwise.common.entity.Category;

import java.util.List;

public interface ICategoryService {
    List<Category> getPerPage(CategoryPageInfo pageInfo, int pageNumber, String sortDirection, String keyword);

    List<Category> listAllUsedInForm();

    Category save(Category category);

    Category getById(Integer id) throws CategoryNotFoundException;

    String checkUniqueness(Integer id, String name, String alias);

    void updateEnabledStatus(Integer id, boolean enabled);

     void delete(Integer id) throws CategoryNotFoundException;
}
