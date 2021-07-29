package com.shopwise.admin.entity.controllers.helpers;

import com.shopwise.admin.processes.FileUpload;
import com.shopwise.common.entity.Product;
import com.shopwise.common.entity.ProductImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class ProductSaveHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductSaveHelper.class);

    public static void setMainImageName(MultipartFile mainImageFile, Product product) {

        LOGGER.info("ProductSaveHelper | setMainImageName is started");

        if (!mainImageFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageFile.getOriginalFilename());

            product.setMainImage(fileName);
        }

        LOGGER.info("ProductSaveHelper | setMainImageName is completed");
    }


    public static void setNewExtraImageNames(MultipartFile[] extraImageFiles, Product product) {

        LOGGER.info("ProductSaveHelper | setNewExtraImageNames is started");

        if (extraImageFiles.length > 0) {
            for (MultipartFile multipartFile : extraImageFiles) {
                if (!multipartFile.isEmpty()) {

                    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

                    if (!product.containsImageName(fileName)) {
                        product.addExtraImage(fileName);
                    }
                }
            }
        }

        LOGGER.info("ProductSaveHelper | setExtraImageNames is completed");
    }


    public static void setExistingExtraImageNames(String[] imageIDs, String[] imageNames,
                                                  Product product) {

        LOGGER.info("ProductSaveHelper | setExistingExtraImageNames is started");

        if (imageIDs == null || imageIDs.length == 0) return;

        Set<ProductImage> images = new HashSet<>();

        for (int count = 0; count < imageIDs.length; count++) {
            Integer id = Integer.parseInt(imageIDs[count]);
            String name = imageNames[count];

            images.add(new ProductImage(id, name, product));
        }

        product.setImages(images);

    }


    public static void saveUploadedImages(MultipartFile mainImageFile,
                                           MultipartFile[] extraImageFiles, Product savedProduct) throws IOException {

        LOGGER.info("ProductSaveHelper | saveUploadedImages is started");

        if (!mainImageFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageFile.getOriginalFilename());

            String uploadDir = "./ShopWiseWebParent/product-images/" + savedProduct.getId();

            FileUpload.cleanDir(uploadDir);

            FileUpload.saveFile(uploadDir, fileName, mainImageFile);
        }

        if (extraImageFiles.length > 0) {

            String uploadDir = "./ShopWiseWebParent/product-images/" + savedProduct.getId() + "/extras";

            for (MultipartFile multipartFile : extraImageFiles) {

                if (multipartFile.isEmpty()) continue;

                String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

                FileUpload.saveFile(uploadDir, fileName, multipartFile);

            }
        }

        LOGGER.info("ProductSaveHelper | saveUploadedImages is completed");
    }

    public static void deleteExtraImagesRemovedOnForm(Product product) {

        LOGGER.info("ProductSaveHelper | deleteExtraImagesRemovedOnForm is started");

        String extraImageDir = "./ShopWiseWebParent/product-images/" + product.getId() + "/extras";
        Path dirPath = Paths.get(extraImageDir);

        try {
            Files.list(dirPath).forEach(file -> {
                String filename = file.toFile().getName();

                if (!product.containsImageName(filename)) {
                    try {
                        Files.delete(file);
                        LOGGER.info("Deleted extra image: " + filename);

                    } catch (IOException e) {
                        LOGGER.error("Could not delete extra image: " + filename);
                    }
                }

            });
        } catch (IOException ex) {
            LOGGER.error("Could not list directory: " + dirPath);
        }
    }

    public static void setProductDetails(String[] detailIDs, String[] detailNames,
                                          String[] detailValues, Product product) {

        if (detailNames == null || detailNames.length == 0) return;

        for (int i = 0; i < detailNames.length; i++) {
            String name = detailNames[i];
            String value = detailValues[i];
            int id = Integer.parseInt(detailIDs[i]);

            if (id != 0) {
                product.addDetail(id, name, value);
            } else if (!name.isEmpty() && !value.isEmpty()) {
                product.addDetail(name, value);
            }
        }

    }
}
