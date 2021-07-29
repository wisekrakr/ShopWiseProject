package com.shopwise.common.services.impl;


import com.shopwise.common.entity.Category;
import com.shopwise.common.exception.CategoryNotFoundException;

import java.util.List;

public interface ICategoryService {

    /**
     * Get a category by id
     * @param id Integer
     * @return category
     */
    Category get(Integer id) throws CategoryNotFoundException;

    /**
     * Get a category by alias
     * @param alias String
     * @return category
     */
    Category get(String alias) throws CategoryNotFoundException;

    /**
     * Find a list of Categories that do not have subcategories, but only a parent or parents.
     * @return a list of categories.
     */
    List<Category> getChildlessCategories();


    /**
     * For sub/child categories, get their parent categories
     * @param child category
     * @return list of parents /grandparents (child->parent->grandparent->....)
     */
    List<Category> getCategoryParents(Category child);
}
