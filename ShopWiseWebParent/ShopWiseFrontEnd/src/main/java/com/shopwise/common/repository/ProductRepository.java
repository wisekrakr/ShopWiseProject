package com.shopwise.common.repository;

import com.shopwise.common.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("SELECT p FROM Product p WHERE p.enabled = true AND p.category.id = ?1 " +
            "OR p.category.parentIDs LIKE %?2% ORDER BY p.name ASC")
    Page<Product> listProductsByCategory(Integer categoryId, String categoryIdMatch, Pageable pageable);

    Product findByAlias(String alias);

    @Query(value = "SELECT * FROM products WHERE enabled = true AND "
            + "MATCH(name, summary, description) AGAINST (?1)",
            nativeQuery = true)
    Page<Product> search(String keyword, Pageable pageable);
}
