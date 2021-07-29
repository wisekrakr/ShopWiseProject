package com.shopwise.admin.entity.services;

import com.shopwise.admin.entity.repositories.CategoryRepository;
import com.shopwise.admin.entity.services.impl.ICategoryService;
import com.shopwise.admin.utils.CategoryNotFoundException;
import com.shopwise.admin.utils.CategoryPageInfo;
import com.shopwise.common.entity.Category;
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
public class CategoryService implements ICategoryService {

    public static final int CATEGORIES_PER_PAGE = 3;

    @Autowired
    private CategoryRepository repository;

    public Category getById(Integer id) throws CategoryNotFoundException {
        try {
            return repository.findById(id).get();

        } catch (NoSuchElementException e) {
            throw new CategoryNotFoundException("Could not find any category with ID: " + id);
        }
    }

    public List<Category> getAll() {
        Sort nameSorting =  Sort.by("name").ascending();

        List<Category> categoryList = new ArrayList<>();
        repository.findAll(nameSorting).forEach(categoryList::add);

        return categoryList;
    }

    @Override
    public List<Category> getPerPage(CategoryPageInfo pageInfo, int pageNumber, String sortDirection, String keyword){
        Sort sort = Sort.by("name");

        if (sortDirection.equals("asc")) {
            sort = sort.ascending();
        } else if (sortDirection.equals("desc")) {
            sort = sort.descending();
        }

        Pageable pageable = PageRequest.of(pageNumber -1, CATEGORIES_PER_PAGE, sort);

        Page<Category> pageCategories;

        if (keyword != null && !keyword.isEmpty()) {
            pageCategories = repository.findAllWithKeyword(keyword, pageable);
        } else {
            pageCategories = repository.findRootCategories(pageable);
        }

        List<Category> rootCategories = pageCategories.getContent();

        pageInfo.setTotalElements(pageCategories.getTotalElements());
        pageInfo.setTotalPages(pageCategories.getTotalPages());

        if (keyword != null && !keyword.isEmpty()) {
            List<Category> searchResult = pageCategories.getContent();
            for (Category category : searchResult) {
                category.setHasChildren(category.getChildren().size() > 0);
            }

            return searchResult;

        } else {
            return listHierarchical(rootCategories, sortDirection);
        }
    }

    /**
     * To avoid making changes in the database, we create a list of copied categories.
     * These categories will have ">>" in front of their name to show the difference in hierarchy.
     * This method exists for aesthetic purposes for the category list page.
     *
     * @param rootCategories list of parent categories
     * @param sortDirection "asc" or "desc" for ascending and descending.
     * @return a new arraylist sorted hierarchical
     */
    private List<Category> listHierarchical(List<Category> rootCategories, String sortDirection) {
        List<Category> hierarchicalCategories = new ArrayList<>();

        for (Category rootCategory : rootCategories) {
            hierarchicalCategories.add(Category.copyFull(rootCategory));

            Set<Category> children = sortChildCategories(rootCategory.getChildren(),sortDirection);
            for (Category child : children) {
                String name = ">>" + child.getName();
                hierarchicalCategories.add(Category.copyFull(child, name));

                listChildrenHierarchical(hierarchicalCategories, child, 1, sortDirection);
            }
        }

        return hierarchicalCategories;
    }

    private void listChildrenHierarchical(List<Category> hierarchicalCategories, Category parent, int subLevel, String sortDirection) {
        Set<Category> children = sortChildCategories(parent.getChildren());

        int level = subLevel + 1;

        for (Category child : children) {
            String name = ">>".repeat(Math.max(0, level)) +
                    child.getName();

            hierarchicalCategories.add(Category.copyFull(child, name));

            listChildrenHierarchical(hierarchicalCategories, child, level, sortDirection);
        }
    }
    private SortedSet<Category> sortChildCategories(Set<Category> children) {
        return sortChildCategories(children,"asc");
    }
    private SortedSet<Category> sortChildCategories(Set<Category> children, String sortDirection) {
        SortedSet<Category> sortedChildren = new TreeSet<>((c1, c2) -> {
            if(sortDirection.equals("asc")){
                return c1.getName().compareTo(c2.getName());
            }else {
                return c2.getName().compareTo(c1.getName());
            }
        });
        sortedChildren.addAll(children);
        return sortedChildren;
    }

    /**
     * This method handles creating a hierarchical list of categories we can place in a select element in the
     * category_form html page. This way a user can spot what category is a parent and what this parents child
     * categories are.
     * {@link #listHierarchical(List, String) <Category>)} is used for the category list page, not in the category form.
     *
     * @return the hierarchical category list.
     */
    @Override
    public List<Category> listAllUsedInForm() {
        List<Category> categoryList = new ArrayList<>();

        Iterable<Category> categoriesInDB = repository.findAll();

        for (Category category : categoriesInDB) {
            if (category.getParent() == null) {
                categoryList.add(Category.copyParentIdAndName(category));

                Set<Category> children = sortChildCategories(category.getChildren());

                for (Category child : children) {
                    String name = ">>" + child.getName();
                    categoryList.add(Category.copyParentIdAndName(child.getId(), name));

                    listChildrenInForm(child, 1, categoryList);
                }
            }
        }

        return categoryList;
    }

    private void listChildrenInForm(Category parent, int subLevel, List<Category> categoryList) {
        int level = subLevel + 1;

        Set<Category> children = sortChildCategories(parent.getChildren());

        for (Category child : children) {
            String name = ">>".repeat(Math.max(0, level)) + child.getName();
            categoryList.add(Category.copyParentIdAndName(child.getId(), name));

            listChildrenInForm(child, level, categoryList);
        }
    }

    /**
     * Method to save a new category to the database
     *
     * @param category category to be saved
     * @return the saved category
     */
    @Override
    public Category save(Category category) {
        Category parent = category.getParent();
        if(parent != null){
            String parentIDs = parent.getParentIDs() == null ? "-" : parent.getParentIDs();
            parentIDs += parent.getId() + "-";
            category.setParentIDs(parentIDs);
        }
        return repository.save(category);
    }

    @Override
    public String checkUniqueness(Integer id, String name, String alias) {
        boolean isNew = (id == null || id == 0);

        Category byAlias = repository.findByAlias(alias);
        if (isNew) {
            if (byAlias != null) return "Duplicate";
        } else {
            if (byAlias != null && !byAlias.getId().equals(id)) return "Duplicate";
        }

        return "OK";
    }

    @Override
    public void updateEnabledStatus(Integer id, boolean enabled){
        repository.toggleEnabledStatus(id, enabled);
    }

    @Override
    public void delete(Integer id) throws CategoryNotFoundException {
        Long countById = repository.countById(id);

        if(countById == null || countById == 0){
            throw new CategoryNotFoundException("Could not find any category with ID: " + id);
        }

        repository.deleteById(id);
    }
}
