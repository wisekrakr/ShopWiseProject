package com.shopwise.common.entity;

import com.shopwise.common.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    public void testListProductsByCategory(){
        String categoryIdMatch = "-" + 1 + "-";
        Pageable pageable = PageRequest.of(1, 10);

        Page<Product> products = repository.listProductsByCategory(10, categoryIdMatch, pageable);

        System.out.println(products.getContent());

        assertThat(products.getTotalElements()).isGreaterThan(0);

    }


}
