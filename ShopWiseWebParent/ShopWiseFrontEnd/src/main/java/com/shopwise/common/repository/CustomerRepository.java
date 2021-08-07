package com.shopwise.common.repository;

import com.shopwise.common.entity.Customer;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends CrudRepository<Customer, Integer> {

    @Query("SELECT c from Customer c WHERE c.email = :email")
    Customer findByEmail(@Param("email") String email);

    @Query("SELECT c from Customer c WHERE c.verificationCode = ?1")
    Customer findByVerificationCode(String code);

    @Query("UPDATE Customer c SET c.enabled = true, c.verificationCode = null WHERE c.id = ?1")
    @Modifying
    void enable(Integer id);

//    @Query("UPDATE Customer c SET c.authenticationType = ?2 WHERE c.id = ?1")
//    @Modifying
//    public void updateAuthenticationType(Integer customerId, AuthenticationType type);

}
