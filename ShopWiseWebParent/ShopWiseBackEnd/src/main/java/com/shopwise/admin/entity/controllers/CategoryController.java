package com.shopwise.admin.entity.controllers;

import com.shopwise.admin.entity.services.CategoryService;
import com.shopwise.admin.processes.FileUpload;
import com.shopwise.admin.processes.export.CategoryCsvExporter;
import com.shopwise.admin.utils.CategoryNotFoundException;
import com.shopwise.admin.utils.CategoryPageInfo;
import com.shopwise.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Controller
public class CategoryController {

    @Autowired
    private CategoryService service;

    /**
     * This getter route will show the first set of categories, sorted by name, ascending.
     * @param model container that contains the data of the application, accessible by view page
     * @return page with list of categories.
     */
    @GetMapping("/categories")
    public String getFirstPageCategories(Model model) {
        return getCategoriesPerPage(1,  "asc",null, model);
    }


    @GetMapping("/categories/page/{pageNumber}")
    public String getCategoriesPerPage(@PathVariable(name = "pageNumber") int pageNumber,
                                       @Param("sortDirection") String sortDirection,
                                       @Param("keyword") String keyword,
                                       Model model) {

        CategoryPageInfo pageInfo = new CategoryPageInfo();
        List<Category> categoryList = service.getPerPage(pageInfo, pageNumber, sortDirection, keyword);

        long startCount = (long) (pageNumber - 1) * CategoryService.CATEGORIES_PER_PAGE + 1;
        long endCount = startCount + CategoryService.CATEGORIES_PER_PAGE - 1;

        if (endCount > pageInfo.getTotalElements()) {
            endCount = pageInfo.getTotalElements();
        }

        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalCount", pageInfo.getTotalElements());
        model.addAttribute("totalPages", pageInfo.getTotalPages());

        model.addAttribute("sortField", "name");
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("sortDirectionReversed", sortDirection.equals("asc") ? "desc" : "asc");

        model.addAttribute("keyword", keyword);

        model.addAttribute("categories", categoryList);
        model.addAttribute("pageTitle", "Categories");

        return "categories/categories";
    }

    /**
     * Get route towards creating a new user.
     * Model will add category, pageTitle, categories for user form
     * @param model container that contains the data of the application, accessible by view page
     * @return route to page with form to create a new category with.
     */
    @GetMapping("/categories/new")
    public String newCategory(Model model) {
        List<Category> categories = service.listAllUsedInForm();

        model.addAttribute("category", new Category());
        model.addAttribute("categories", categories);
        model.addAttribute("pageTitle", "Create a new Category");
        model.addAttribute("editMode", false);


        return "categories/category_form";
    }

    /**
     * Add a new category to the database
     *
     * @param category           the new category that will be saved
     * @param redirectAttributes to add a message to the element
     * @param multipartFile      to obtain the file object, we use @RequestParam. Html input element has name "image" attached.
     * @return route to category list page
     */
    @PostMapping("/categories/save")
    public String saveCategory(Category category, RedirectAttributes redirectAttributes, @RequestParam("fileImage") MultipartFile multipartFile) throws IOException {

        //if entity is new, so if category is not being edited
        if(!multipartFile.isEmpty()){
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            category.setImage(fileName);

            Category categoryQuickSave = service.save(category);

            // path to category images directory
            String dir = "./ShopWiseWebParent/category-images/" + categoryQuickSave.getId();

            FileUpload.cleanDir(dir);
            // save the new file/image to the category image directory
            FileUpload.saveFile(dir, fileName, multipartFile);
        }else{
            service.save(category);
        }


        redirectAttributes.addFlashAttribute("message", "Category has been saved successfully!");

        return "redirect:/categories";
    }

    /**
     * Get route towards editing an existing category.
     * @param id category id
     * @param model container that contains the data of the application, accessible by view page
     * @param redirectAttributes add messages with
     * @return route to page with form to edit category.
     */
    @GetMapping("/categories/edit/{id}")
    public String updateCategory(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            List<Category> categories = service.listAllUsedInForm();

            Category category = service.getById(id);
            model.addAttribute("category", category);
            model.addAttribute("categories", categories);
            model.addAttribute("pageTitle", "Update category " + category.getName());
            model.addAttribute("editMode", true);

            return "categories/category_form";

        } catch (CategoryNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());

            return "redirect:/categories";
        }
    }

    /**
     * Get route to toggle enabled status for category with.
     * @param id category id
     * @param enabled boolean
     * @param redirectAttributes to add messages to the view
     * @return route to category list page.
     */
    @GetMapping("/categories/{id}/enabled/{status}")
    public String updateCategoryStatus(@PathVariable(name = "id") Integer id,
                                   @PathVariable(name = "status") boolean enabled,
                                   RedirectAttributes redirectAttributes) {

        service.updateEnabledStatus(id, enabled);

        String message = enabled ? "Category with ID: " + id + " is enabled" : "Category with ID: " + id + " is disabled";

        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/categories";
    }

    /**
     * Get route towards deleting a category that has no children.
     * @param id category id
     * @param redirectAttributes to add messages to view with.
     * @return route to category list page.
     */
    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            Category category = service.getById(id);

            if(category.isHasChildren()){
                redirectAttributes.addFlashAttribute(
                        "message",
                        "Category : " + id + " cannot be removed. This category is the parent of one or more subcategories"
                );
                return "redirect:/categories";
            }else{
                //delete category
                service.delete(id);
                //category images directory
                String dir = "./ShopWiseWebParent/category-images/" + id;
                //remove category image directory of this category
                FileUpload.removeDir(dir);

                redirectAttributes.addFlashAttribute(
                        "message",
                        "Category : " + id + " successfully removed."
                );
            }
        } catch (CategoryNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/categories";
    }

    @GetMapping("/categories/export/csv")
    public void exportCSV(HttpServletResponse response){
        CategoryCsvExporter exporter = new CategoryCsvExporter();

        try {
            exporter.export(service.getAll(),response);
        }catch (Throwable t){
            throw new IllegalStateException("Could not export users CSV file",t);
        }
    }



}
