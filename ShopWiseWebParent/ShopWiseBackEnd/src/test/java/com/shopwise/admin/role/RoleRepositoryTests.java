package com.shopwise.admin.role;

import com.shopwise.admin.entity.repositories.RoleRepository;
import com.shopwise.common.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class RoleRepositoryTests {

    @Autowired
    private RoleRepository repository;

    @Test
    public void testCreateFirstRole(){
        Role roleAdmin = new Role("Admin", "Manage all features.");
        Role savedRole = repository.save(roleAdmin);

        assertThat(savedRole.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateRestRoles(){

        repository.saveAll(
                List.of(
                    new Role("Sales", "Manage product price, customers, shipping, orders & sales report."),
                    new Role("Editor", "Manage categories, brands, products, articles & menus."),
                    new Role("Shipper", "View products and orders, Update order statuses."),
                    new Role("Assistant", "Manage questions and reviews.")
                )
        );
    }

    @Test
    public void testGetAllRoles(){
        Iterable<Role>roles = repository.findAll();
        roles.forEach(System.out::println);


    }
}
