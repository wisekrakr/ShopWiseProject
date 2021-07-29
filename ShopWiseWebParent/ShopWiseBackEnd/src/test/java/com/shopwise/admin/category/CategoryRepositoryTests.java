package com.shopwise.admin.category;

import com.shopwise.admin.entity.repositories.CategoryRepository;
import com.shopwise.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryRepositoryTests.class);

    @Autowired
    private CategoryRepository repository;

    @Test
    public void testCreateRootCategory(){
        Category category = new Category("Electronics","electronics","default.png");
        Category savedCategory = repository.save(category);

        assertThat(savedCategory.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateSubcategory(){
        Category parent = new Category(2);
        Category child1 = new Category("Cameras","cameras","default.png",parent);
        Category child2 = new Category("Monitors","monitors","default.png",parent);
        Category child3 = new Category("Tablets","tablets","default.png",parent);
        Category child4 = new Category("Computer Components","computer_components","default.png",child1);

        repository.saveAll(List.of(child1,child2,child3,child4));
    }



    @Test
    public void testGetCategory(){
        Category category = repository.findById(19).get();
        System.out.println("PARENT = " + category.getParent());
//
//        Set<Category> children = category.getChildren();
//        for(Category child : children){
//            System.out.println("CHILD "+ child.getId() + " = " + child.getName());
//            if(!child.getChildren().isEmpty() ){
//                for(Category baby: child.getChildren())
//                System.out.println("GRAND-CHILD "+ baby.getId() + " = " + baby.getName());
//
//            }
//        }
//
//        assertThat(children.size()).isGreaterThan(0);

    }

    @Test
    public void testPrintHierarchicalCategories(){
        Iterable<Category>categories = repository.findAll();

        for(Category category: categories){
            if(category.getParent() == null){
                System.out.println(category.getName());

                Set<Category> children = category.getChildren();

                for(Category child : children){
                    System.out.println(">>" + child.getName());
                    printChildren(child,1);
                }
            }
        }
    }

    private void printChildren(Category parent, int subLevel){
        int level = subLevel + 1;

        for (Category category: parent.getChildren()){
            for (int i = 0; i < level; i++){
                System.out.print(">>");
            }
            System.out.println(category.getName());

            printChildren(category, level);
        }
    }

    @Test
    public void testCreateDirectory() throws IOException {
        String fileName = "/../category-images/21";

        Path path = Paths.get(fileName);

        if (!Files.exists(path)) {

            Files.createDirectories(path);
            System.out.println("Directory created");
        } else {

            System.out.println("Directory already exists");
        }
    }

    @Test
    public void testCreateDirectoryAgain()  {
        String rootDir = "../category-images/test";

        String filePath = rootDir;
        try {
            if(rootDir.contains(File.separator)){
                filePath = rootDir.substring(0, rootDir.lastIndexOf(File.separator));
            }
            File file = new File(filePath);
            if(!file.exists()) {
                System.out.println(file.mkdirs());
                file = new File(rootDir);
                System.out.println(file.createNewFile());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testSaveFileAtLocation(){
        Category category = repository.findById(15).get();

        MockMultipartFile multipartFile
                = new MockMultipartFile(
                "books",
                "books.png",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

        category.setImage(fileName);

        // path to user avatar directory
        String dir = "../category-images/" + category.getId();
        Path uploadPath = Paths.get(dir);

        System.out.println("UPLOAD PATH: " + uploadPath);
        Path directory = null;
        try {
            if (!Files.exists(uploadPath)) {
                directory = Files.createDirectories(uploadPath);


            }
        } catch (Throwable t) {
            LOGGER.error("Could not create new directory for file upload", t);
//            throw new IllegalArgumentException("Could not create new directory for file upload", t);
        }

        assertThat(directory).isDirectory().isNotNull();

        try (InputStream in =  multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Throwable t) {
            LOGGER.error("Could not upload File", t);
//            throw new IllegalStateException("Could not upload File", t);
        }
    }

    @Test
    public void testGetRootCategories(){
        List<Category>categoriesList = repository.findRootCategories(Sort.by("name").ascending());

        categoriesList.forEach(System.out::println);
    }

    @Test
    public void testGetCategoryByName(){
        String name = "Video Games";
        Category category = repository.findByName(name);

        assertThat(category).isNotNull();
        assertThat(category.getName()).isEqualTo(name);
    }

    @Test
    public void testGetCategoryByAlias(){
        String name = "vid";
        Category category = repository.findByAlias(name);

        assertThat(category).isNotNull();
        assertThat(category.getAlias()).contains(name);
    }

    @Test void testPuttingParentIDsOnCategory(){
        Iterable<Category> all = repository.findAll();

        for (Category category: all){
            repository.save(category);
        }

    }
}
