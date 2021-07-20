package com.shopwise.admin.brand;

import com.shopwise.admin.entity.repositories.BrandRepository;
import com.shopwise.common.entity.Brand;
import com.shopwise.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class BrandRepositoryTests {

    @Autowired
    private BrandRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateBrand(){
        Category category = entityManager.find(Category.class,3);

        Brand brand = new Brand("Acer","default-brand.png");
        brand.addCategory(category);

        Brand savedBrand = repository.save(brand);

        assertThat(savedBrand.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateNewBrandWithTwoCategories(){
        Brand brand = new Brand("Samsung","default-brand.png");
        brand.addCategory(new Category(14));
        brand.addCategory(new Category(31));

        Brand savedBrand = repository.save(brand);

        assertThat(savedBrand.getId()).isGreaterThan(0);
    }

    @Test
    public void testGetAllBrands(){
        Iterable<Brand>brands = repository.findAll();
        brands.forEach(System.out::println);
    }

    @Test
    public void testCountById(){
        Integer id = 1;
        Long countById = repository.countById(id);

        assertThat(countById).isNotNull().isGreaterThan(0);
    }

    @Test
    public void testUpdateBrand(){
        Brand brand = repository.findById(2).get();

        brand.setName("Apple");

        Brand savedBrand = repository.save(brand);

        assertThat(savedBrand).isNotNull();
    }

    @Test
    public void testDeleteBrand(){
        Brand brand = repository.findById(1).get();

        repository.delete(brand);

        Optional<Brand> byId = repository.findById(1);

        assertThat(byId.isEmpty());
    }
}
