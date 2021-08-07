package com.shopwise.admin.entity.repositories;

import com.shopwise.common.entity.Customer;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

//public interface CustomerRepository extends CrudRepository<Customer, Integer> {
//
//    @Query("SELECT c from Customer c WHERE c.email = :email")
//    Customer findByEmail(@Param("email") String email);
//
//    @Query("SELECT c from Customer c WHERE c.verification_code = :code")
//    Customer findByVerificationCode(String code);
//
//    @Query("UPDATE Customer c SET c.enabled = ?2 WHERE c.id = ?1")
//    @Modifying
//    void toggleEnabledStatus(Integer id, boolean enabled);
//}
