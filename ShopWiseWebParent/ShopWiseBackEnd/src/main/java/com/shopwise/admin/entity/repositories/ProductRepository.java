package com.shopwise.admin.entity.repositories;

import com.shopwise.common.entity.Product;
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

    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1% "
            + "OR p.summary LIKE %?1% "
            + "OR p.description LIKE %?1% "
            + "OR p.brand.name LIKE %?1% "
            + "OR p.category.name LIKE %?1%")
    Page<Product> findAll(String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category.id = ?1 "
            + "OR p.category.parentIDs LIKE %?2%")
    Page<Product> findAllInCategory(Integer categoryId, String categoryIdMatch,
                                           Pageable pageable);

    @Query("SELECT p FROM Product p WHERE (p.category.id = ?1 "
            + "OR p.category.parentIDs LIKE %?2%) AND "
            + "(p.name LIKE %?3% "
            + "OR p.summary LIKE %?3% "
            + "OR p.description LIKE %?3% "
            + "OR p.brand.name LIKE %?3% "
            + "OR p.category.name LIKE %?3%)")
    Page<Product> searchInCategory(Integer categoryId, String categoryIdMatch,
                                          String keyword, Pageable pageable);

    @Query("UPDATE Product p SET p.enabled = ?2 WHERE p.id = ?1")
    @Modifying // in ProductService use @Transactional for the class
    void toggleEnabledStatus(Integer id, boolean enabled);
}
