package com.shopwise.common.entity;

import com.shopwise.common.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository repository;

    @Test
    public void testGetAllProducts(){
        Iterable<Product> products = repository.findAll();

        products.forEach(System.out::println);
    }
}
