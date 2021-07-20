package com.shopwise.admin.entity.controllers;

import com.shopwise.admin.entity.services.BrandService;
import com.shopwise.admin.entity.services.CategoryService;
import com.shopwise.admin.processes.FileUpload;
import com.shopwise.admin.processes.export.BrandCsvExporter;
import com.shopwise.admin.utils.BrandNotFoundException;
import com.shopwise.common.entity.Brand;
import com.shopwise.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

/**
 * Brand Controller capable of handling the HTTP requests and
 * establish the routing table to know which methods serve which endpoints.
 */

@Controller
public class BrandController {

    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;

    /**
     * This getter route will show the first set of brands, sorted by name, ascending.
     * @param model container that contains the data of the application, accessible by view page
     * @return page with list of brands.
     */
    @GetMapping("/brands")
    public String getFirstPageBrands(Model model) {
        return getBrandsPage(1,  "name","asc",null, model);
    }

    @GetMapping("/brands/page/{pageNumber}")
    public String getBrandsPage(@PathVariable(name = "pageNumber") int pageNumber,
                                @Param("sortField") String sortField,
                                @Param("sortDirection") String sortDirection,
                                @Param("keyword") String keyword,
                                Model model) {

        Page<Brand> page = brandService.getByPage(pageNumber, sortField, sortDirection,keyword);
        List<Brand> brandList = page.getContent();

        long startCount = (long) (pageNumber - 1) * BrandService.BRANDS_PER_PAGE + 1;
        long endCount = startCount + BrandService.BRANDS_PER_PAGE - 1;

        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalCount", page.getTotalElements());
        model.addAttribute("totalPages", page.getTotalPages());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("sortDirectionReversed", sortDirection.equals("asc") ? "desc" : "asc");

        model.addAttribute("keyword", keyword);

        model.addAttribute("brands", brandList);
        model.addAttribute("pageTitle", "Brands");

        return "brands/brands";
    }

    /**
     * Get route towards creating a new brand.
     * Model will add brand, list of categories, page title, and a boolean to see if we are updating a existing user.
     * @param model container that contains the data of the application, accessible by view page
     * @return route to page with form to create a new brand with.
     */
    @GetMapping("/brands/new")
    public String newBrand(Model model) {
        List<Category> categoryList = categoryService.listAllUsedInForm();

        System.out.println("CATEGORIES: " + categoryList.size());

        model.addAttribute("brand", new Brand());
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("pageTitle", "Create a new Brand");
        model.addAttribute("editMode", false);

        return "brands/brand_form";
    }

    /**
     * Add a new user to the database
     *
     * @param brand              the new brand that will be saved
     * @param redirectAttributes to add a message to the element
     * @param multipartFile      to obtain the file object, we use @RequestParam. Html input element has name "fileImage" attached.
     * @return route to brands list page
     */
    @PostMapping("/brands/save")
    public String saveBrand(Brand brand, RedirectAttributes redirectAttributes, @RequestParam("fileImage") MultipartFile multipartFile) throws IOException {

        if(!multipartFile.isEmpty()){
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            brand.setLogo(fileName);

            Brand brandQuickSave = brandService.save(brand);

            // path to brand logo directory
            String dir = "./ShopWiseWebParent/brand-logos/" + brandQuickSave.getId();

            // clean this brand logo directory
            FileUpload.cleanDir(dir);
            // save the new file/logo to the brand logo directory
            FileUpload.saveFile(dir, fileName, multipartFile);
        }else{
            brandService.save(brand);
        }


        redirectAttributes.addFlashAttribute("message", "The brand has been saved successfully!");

        return "redirect:/brands";
    }

    /**
     * Get route towards editing an existing brand.
     * @param id brand id
     * @param model container that contains the data of the application, accessible by view page
     * @param redirectAttributes add messages with
     * @return route to page with form to edit brand.
     */
    @GetMapping("/brands/edit/{id}")
    public String updateBrand(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            List<Category> categoryList = categoryService.listAllUsedInForm();

            Brand brand = brandService.getById(id);
            model.addAttribute("brand", brand);
            model.addAttribute("categoryList", categoryList);
            model.addAttribute("pageTitle", "Update " + brand.getName());
            model.addAttribute("editMode", true);

            return "brands/brand_form";

        } catch (BrandNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());

            return "redirect:/brands";
        }
    }

    /**
     * Get route towards deleting a brand.
     * @param id brand id
     * @param redirectAttributes to add messages to view with.
     * @return route to user list page.
     */
    @GetMapping("/brands/delete/{id}")
    public String deleteBrand(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            Brand brand = brandService.getById(id);

            brandService.delete(id);

            FileUpload.removeDir("./ShopWiseWebParent/brand-logos/" + id);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Brand : " + brand.getName() + " successfully removed."
            );
        } catch (BrandNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/brands";

    }

    @GetMapping("/brands/export/csv")
    public void exportCSV(HttpServletResponse response){
        BrandCsvExporter exporter = new BrandCsvExporter();

        try {
            exporter.export(brandService.getAll(),response);
        }catch (Throwable t){
            throw new IllegalStateException("Could not export brands CSV file",t);
        }
    }

}
