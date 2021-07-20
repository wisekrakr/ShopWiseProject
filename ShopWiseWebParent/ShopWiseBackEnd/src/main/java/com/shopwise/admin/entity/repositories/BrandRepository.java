package com.shopwise.admin.entity.repositories;

import com.shopwise.common.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends PagingAndSortingRepository<Brand, Integer> {

    @Query("SELECT b from Brand b WHERE b.name LIKE %?1%")
    Page<Brand> findAllWithKeyword(String keyword, Pageable pageable);

    Brand findByName(@Param("name") String name);

    Long countById(Integer id);

    @Query("SELECT NEW Brand(b.id, b.name) FROM Brand b ORDER BY b.name ASC")
    List<Brand>findAll();
}
