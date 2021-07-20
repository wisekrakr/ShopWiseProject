package com.shopwise.admin.entity.repositories;

import com.shopwise.common.entity.Category;
import com.shopwise.common.entity.Product;
import com.shopwise.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {

    Long countById(Integer id);

    Product findByName(@Param("name") String name);

    Product findByAlias(@Param("alias") String alias);

    @Query("SELECT p from Product p WHERE CONCAT(p.name, ' ', p.summary, ' ', p.category, ' ', p.brand) LIKE %?1%")
    Page<Product> findAllWithKeyword(String keyword, Pageable pageable);

    @Query("UPDATE Product p SET p.enabled = ?2 WHERE p.id = ?1")
    @Modifying // in ProductService use @Transactional for the class
    void toggleEnabledStatus(Integer id, boolean enabled);
}
