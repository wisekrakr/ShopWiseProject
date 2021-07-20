package com.shopwise.admin.entity.repositories;

import com.shopwise.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Integer> {
    @Query("SELECT u from User u WHERE u.email = :email")
    User findByEmail(@Param("email") String email);

    Long countById(Integer id);

    //query represent the fist parameter in the method: single keyword
//    @Query("SELECT u from User u WHERE u.firstName LIKE %?1% OR u.lastName LIKE %?1%"
//            + " OR u.email LIKE %?1% OR u.id LIKE %?1%")
    @Query("SELECT u from User u WHERE CONCAT(u.id, ' ', u.email, ' ', u.firstName, ' ', u.lastName) LIKE %?1%")
    Page<User> findAllWithKeyword(String keyword, Pageable pageable);

    @Query("UPDATE User u SET u.enabled = ?2 WHERE u.id = ?1")
    @Modifying // in UserService use @Transactional for the class
    void toggleEnabledStatus(Integer id, boolean enabled);
}
