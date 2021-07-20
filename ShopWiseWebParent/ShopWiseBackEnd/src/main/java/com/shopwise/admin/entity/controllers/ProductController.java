package com.shopwise.admin.entity.controllers;

import com.shopwise.admin.entity.controllers.helpers.ProductSaveHelper;
import com.shopwise.admin.entity.services.BrandService;
import com.shopwise.admin.entity.services.CategoryService;
import com.shopwise.admin.entity.services.ProductService;
import com.shopwise.admin.processes.FileUpload;
import com.shopwise.admin.utils.ProductNotFoundException;
import com.shopwise.common.entity.Brand;
import com.shopwise.common.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class ProductController {

    @Autowired(required=true)
    private ProductService service;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/products")
    public String getAll(Model model){
        List<Product> products = service.getAll();

        model.addAttribute("products", products);

        model.addAttribute("pageTitle", "Products");

        return "products/products";
    }

    /**
     * Get route towards creating a new product.
     * Model will add product, pageTitle, categories and brands for product form
     * @param model container that contains the data of the application, accessible by view page
     * @return route to page with form to create a new product with.
     */
    @GetMapping("/products/new")
    public String newProduct(Model model) {
        List<Brand> brands = brandService.getAll();

        Product product = new Product();
        product.setEnabled(true);

        model.addAttribute("product", product);
        model.addAttribute("brands", brands);
        model.addAttribute("pageTitle", "Create a new Product");
        model.addAttribute("editMode", false);


        return "products/product_form";
    }

    /**
     * Add a new product to the database
     *
     * @param product the new product that will be saved
     * @param attributes to add a message to the element
     * @return route to product list page
     */
    @PostMapping("/products/save")
    public String saveProduct(Product product, RedirectAttributes attributes,
                              @RequestParam("fileImage") MultipartFile mainImageFile,
                              @RequestParam("extraFileImage") MultipartFile[] extraImageFiles,
                              @RequestParam(name = "detailIDs", required = false) String[] detailIDs,
                              @RequestParam(name = "detailNames", required = false) String[] detailNames,
                              @RequestParam(name = "detailValues", required = false) String[] detailValues,
                              @RequestParam(name = "imageIDs", required = false) String[] imageIDs,
                              @RequestParam(name = "imageNames", required = false) String[] imageNames) throws IOException {

        ProductSaveHelper.setMainImageName(mainImageFile, product);
        ProductSaveHelper.setExistingExtraImageNames(imageIDs, imageNames, product);
        ProductSaveHelper.setNewExtraImageNames(extraImageFiles, product);
        ProductSaveHelper.setProductDetails(detailIDs, detailNames, detailValues, product);

        Product productQuickSave = service.save(product);

        ProductSaveHelper.saveUploadedImages(mainImageFile, extraImageFiles, productQuickSave);
        ProductSaveHelper.deleteExtraImagesRemovedOnForm(product);

        attributes.addFlashAttribute("message", "Product saved successfully!");

        return "redirect:/products";
    }

    /**
     * Get route towards editing an existing product.
     * @param id product id
     * @param model container that contains the data of the application, accessible by view page
     * @param redirectAttributes add messages with
     * @return route to page with form to edit product.
     */
    @GetMapping("/products/edit/{id}")
    public String updateProduct(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {

            Product product = service.getById(id);
            model.addAttribute("category", product);
            model.addAttribute("pageTitle", "Update product " + product.getName());
            model.addAttribute("editMode", true);

            return "products/product_form";

        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());

            return "redirect:/products";
        }
    }

    /**
     * Get route to toggle enabled status for product with.
     * @param id product id
     * @param enabled boolean
     * @param redirectAttributes to add messages to the view
     * @return route to product list page.
     */
    @GetMapping("/products/{id}/enabled/{status}")
    public String updateProductStatus(@PathVariable(name = "id") Integer id,
                                       @PathVariable(name = "status") boolean enabled,
                                       RedirectAttributes redirectAttributes) {

        service.updateEnabledStatus(id, enabled);

        String message = enabled ? "Product with ID: " + id + " is enabled" : "Product with ID: " + id + " is disabled";

        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/products";
    }

    /**
     * Get route towards deleting a product that has no children.
     * @param id product id
     * @param redirectAttributes to add messages to view with.
     * @return route to product list page.
     */
    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            service.delete(id);

            FileUpload.removeDir("./ShopWiseWebParent/product-images/" + id);
            FileUpload.removeDir("./ShopWiseWebParent/product-images/" + id + "/extras/");

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Product : " + id + " successfully removed."
            );
        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/products";
    }

}
