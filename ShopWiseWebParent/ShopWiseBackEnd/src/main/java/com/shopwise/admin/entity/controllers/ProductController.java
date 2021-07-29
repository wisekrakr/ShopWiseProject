package com.shopwise.admin.entity.controllers;

import com.shopwise.admin.entity.controllers.helpers.ProductSaveHelper;
import com.shopwise.admin.entity.services.BrandService;
import com.shopwise.admin.entity.services.CategoryService;
import com.shopwise.admin.entity.services.ProductService;
import com.shopwise.admin.processes.FileUpload;
import com.shopwise.admin.security.UserAccountDetails;
import com.shopwise.admin.utils.ProductNotFoundException;
import com.shopwise.common.entity.Brand;
import com.shopwise.common.entity.Category;
import com.shopwise.common.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;
    private final BrandService brandService;
    private final CategoryService categoryService;


    @Autowired
    public ProductController(ProductService productService, BrandService brandService,
                             CategoryService categoryService) {
        super();
        this.productService = productService;
        this.brandService = brandService;
        this.categoryService = categoryService;
    }

    @GetMapping("/products")
    public String getFirstPageProducts(Model model) {
        LOGGER.info("ProductController | getFirstPageProducts is started");
        return getProductsPerPage(1, "name", "asc", null, 0, model);
    }


    @GetMapping("/products/page/{pageNumber}")
    public String getProductsPerPage(
            @PathVariable(name = "pageNumber") int pageNumber,
            @Param("sortField") String sortField,
            @Param("sortDirection") String sortDirection,
            @Param("keyword") String keyword,
            @Param("categoryId") Integer categoryId,
            Model model
    ) {

        LOGGER.info("ProductController | getProductsPerPage is started");

        Page<Product> page = productService.getByPage(pageNumber, sortField, sortDirection, keyword, categoryId);
        List<Product> products = page.getContent();

        List<Category> categories = categoryService.listAllUsedInForm();

        long startCount = (long) (pageNumber - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
        long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;

        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        String reverseSortDirection = sortDirection.equals("asc") ? "desc" : "asc";

        if (categoryId != null) model.addAttribute("categoryId", categoryId);

        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalCount", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDirection", reverseSortDirection);
        model.addAttribute("keyword", keyword);
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);

        return "products/products";
    }

    /**
     * Get route towards creating a new product.
     * Model will add product, pageTitle, categories and brands for product form
     *
     * @param model container that contains the data of the application, accessible by view page
     * @return route to page with form to create a new product with.
     */
    @GetMapping("/products/new")
    public String newProduct(Model model) {
        LOGGER.info("ProductController | newProduct is started");

        List<Brand> brands = brandService.getAll();

        Product product = new Product();
        product.setEnabled(true);

        LOGGER.info("ProductController | newProduct | product : " + product);

        model.addAttribute("product", product);
        model.addAttribute("brands", brands);
        model.addAttribute("pageTitle", "Create a new Product");
        model.addAttribute("numberOfExistingExtraImages", 0);
        model.addAttribute("editMode", false);


        return "products/product_form";
    }

    /**
     * Add a new product to the database
     *
     * @param product    the new product that will be saved
     * @param attributes to add a message to the element
     * @return route to product list page
     */
    @PostMapping("/products/save")
    public String saveProduct(Product product, RedirectAttributes attributes,
                              @RequestParam(value = "fileImage", required = false) MultipartFile mainImageFile,
                              @RequestParam(value = "extraFileImage", required = false) MultipartFile[] extraImageFiles,
                              @RequestParam(name = "detailIDs", required = false) String[] detailIDs,
                              @RequestParam(name = "detailNames", required = false) String[] detailNames,
                              @RequestParam(name = "detailValues", required = false) String[] detailValues,
                              @RequestParam(name = "imageIDs", required = false) String[] imageIDs,
                              @RequestParam(name = "imageNames", required = false) String[] imageNames,
                              @AuthenticationPrincipal UserAccountDetails activeUser) throws IOException {

        LOGGER.info("ProductController | saveProduct is started");

        if(activeUser.hasRole("Sales")){
            productService.saveProductPrice(product);

            attributes.addFlashAttribute("message", "Product saved successfully!");

            return "redirect:/products";
        }

        ProductSaveHelper.setMainImageName(mainImageFile, product);
        ProductSaveHelper.setExistingExtraImageNames(imageIDs, imageNames, product);
        ProductSaveHelper.setNewExtraImageNames(extraImageFiles, product);
        ProductSaveHelper.setProductDetails(detailIDs, detailNames, detailValues, product);

        Product productQuickSave = productService.save(product);

        ProductSaveHelper.saveUploadedImages(mainImageFile, extraImageFiles, productQuickSave);
        ProductSaveHelper.deleteExtraImagesRemovedOnForm(product);

        attributes.addFlashAttribute("message", "Product saved successfully!");

        return "redirect:/products";
    }

    /**
     * Get route towards editing an existing product.
     *
     * @param id                 product id
     * @param model              container that contains the data of the application, accessible by view page
     * @param redirectAttributes add messages with
     * @return route to page with form to edit product.
     */
    @GetMapping("/products/edit/{id}")
    public String updateProduct(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {

        LOGGER.info("ProductController | updateProduct is started");

        try {
            Product product = productService.getById(id);
            List<Brand> brands = brandService.getAll();
            LOGGER.info("ProductController | editProduct | product  : " + product);

            model.addAttribute("product", product);
            model.addAttribute("brands", brands);
            model.addAttribute("pageTitle", "Update product " + product.getName());
            model.addAttribute("numberOfExistingExtraImages", product.getImages().size());
            model.addAttribute("editMode", true);

            return "products/product_form";

        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());

            return "redirect:/products";
        }
    }

    /**
     * Get route to toggle enabled status for product with.
     *
     * @param id                 product id
     * @param enabled            boolean
     * @param redirectAttributes to add messages to the view
     * @return route to product list page.
     */
    @GetMapping("/products/{id}/enabled/{status}")
    public String updateProductStatus(@PathVariable(name = "id") Integer id,
                                      @PathVariable(name = "status") boolean enabled,
                                      RedirectAttributes redirectAttributes) {
        LOGGER.info("ProductController | updateProductStatus is started");

        productService.updateEnabledStatus(id, enabled);

        String status = enabled ? "Product with ID: " + id + " is enabled" : "Product with ID: " + id + " is disabled";

        LOGGER.info("ProductController | updateProductStatus | status : " + status);

        redirectAttributes.addFlashAttribute("message", status);

        return "redirect:/products";
    }

    /**
     * Get route to view product details
     *
     * @param id                 product id
     * @param model              container that contains the data of the application, accessible by view page
     * @param redirectAttributes add messages with
     * @return
     */
    @GetMapping("/products/details/{id}")
    public String viewProductDetails(@PathVariable("id") Integer id, Model model,
                                     RedirectAttributes redirectAttributes) {

        LOGGER.info("ProductController | viewProductDetails is started");

        try {
            Product product = productService.getById(id);

            LOGGER.info("ProductController | viewProductDetails  | product : " + product.toString());

            model.addAttribute("product", product);

            return "products/product_details_modal";

        } catch (ProductNotFoundException e) {

            LOGGER.info("ProductController | viewProductDetails  | messageError : " + e.getMessage());

            redirectAttributes.addFlashAttribute("messageError", e.getMessage());

            return "redirect:/products";
        }
    }

    /**
     * Get route towards deleting a product that has no children.
     *
     * @param id                 product id
     * @param redirectAttributes to add messages to view with.
     * @return route to product list page.
     */
    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {

        LOGGER.info("ProductController | deleteProduct is started");

        try {
            productService.delete(id);

            FileUpload.removeDir("./ShopWiseWebParent/product-images/" + id);
            FileUpload.removeDir("./ShopWiseWebParent/product-images/" + id + "/extras/");

            LOGGER.info("ProductController | deleteProduct is done");

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Product : " + id + " successfully removed."
            );
        } catch (ProductNotFoundException e) {
            LOGGER.info("ProductController | deleteProduct | message : " + e.getMessage());

            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/products";
    }

}
