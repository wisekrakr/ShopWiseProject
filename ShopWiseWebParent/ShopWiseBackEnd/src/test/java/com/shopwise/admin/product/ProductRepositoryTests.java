package com.shopwise.admin.product;

import com.shopwise.admin.entity.repositories.ProductRepository;
import com.shopwise.common.entity.Brand;
import com.shopwise.common.entity.Category;
import com.shopwise.common.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateProduct() {
        Brand brand = entityManager.find(Brand.class, 2);
        Category category = entityManager.find(Category.class, 1);

        Product product = new Product();
        product.setName("iMac 27");
        product.setAlias("imac_27_inch");
        product.setSummary("Ready for big things");
        product.setDescription("The 27‑inch iMac is packed with powerful tools and apps that let you take any idea to the next level. Its superfast processors and graphics, massive memory, and all-flash storage can tackle any workload with ease. And with its advanced audio and video capabilities and stunning 5K Retina display, everything you do is larger than life.");
        product.setBrand(brand);
        product.setCategory(category);
        product.setPrice(1799F);
        product.setCost(500F);
        product.setEnabled(true);
        product.setStock(33);
        product.setCreatedAt(new Date());
        product.setUpdatedAt(new Date());

        Product save = repository.save(product);

        assertThat(save).isNotNull();
        assertThat(save.getId()).isGreaterThan(0);
    }

    @Test
    public void testGetAllProducts(){
        Iterable<Product> products = repository.findAll();

        products.forEach(System.out::println);
    }

    @Test
    public void testGetProductById(){
        Product product = repository.findById(3).get();

        assertThat(3).isEqualTo(product.getId());
        assertThat(product.isInStock()).isTrue();
    }

    @Test
    public void testUpdateProduct(){
        Product product = repository.findById(1).get();
        product.setStock(50);

        Product savedProduct = repository.save(product);

        assertThat(savedProduct).isNotNull();
        assertThat(product.getId()).isEqualTo(savedProduct.getId());
    }

    @Test
    public void testFindByNameOrAlias(){
        Product grim = repository.findByName("Grim Fandango");
        Product redemption = repository.findByAlias("red_dead_redemption_2");

        System.out.println(grim);
        System.out.println(redemption);

        assertThat(grim).isNotNull();
        assertThat(redemption.getId()).isGreaterThan(0);
    }

    @Test
    public void testSaveProductWithImages(){
        Integer id = 2;
        Product product = repository.findById(id).get();

        product.setMainImage("default_image.png");

        Product savedProduct = repository.save(product);

        assertThat(savedProduct.getId()).isEqualTo(id);
        assertThat(savedProduct.getMainImage()).isNotEmpty();
    }
}
