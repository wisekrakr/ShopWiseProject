package com.shopwise.admin.user;

import com.shopwise.admin.entity.repositories.UserRepository;
import com.shopwise.common.entity.Role;
import com.shopwise.common.entity.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {

    @Autowired
    private UserRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateUser(){
        Role role = entityManager.find(Role.class,1);

        User user = new User(
                "wisekrakr@shop.com",
                "06-1111111",
                "12345678",
                "David",
                "Buendia"
        );
        user.addRole(role);

        User savedUser = repository.save(user);

        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateNewUserWithTwoRoles(){
        User user = new User(
                "manny@rubacava.mx",
                "06-2222222",
                "12345678",
                "Manuel",
                "Calavera"
        );
        user.addRole(new Role(3));
        user.addRole(new Role(5));

        User savedUser = repository.save(user);

        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testGetAllUsers(){
        Iterable<User>users = repository.findAll();
        users.forEach(System.out::println);
    }

    @Test
    public void testGetUserByEmail(){
        String email = "wisekrakr@shop.com";
        User user = repository.findByEmail(email);

        assertThat(user).isNotNull();
    }

    @Test
    public void testCountById(){
        Integer id = 1;
        Long countById = repository.countById(id);

        assertThat(countById).isNotNull().isGreaterThan(0);
    }

    @Test
    public void testToggleUserStatus(){
        User user = repository.findById(1).get();

        repository.toggleEnabledStatus(user.getId(), !user.isEnabled());
    }



    @Test
    public void testListFirstPageUsers(){
        int pageNumber = 1;
        int pageSize = 3;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<User>page = repository.findAll(pageable);

        List<User>users = page.getContent();

        users.forEach(System.out::println);

        assertThat(users.size()).isEqualTo(pageSize);
    }

    @Test
    public void testSearchUsers(){
        String keyword = "david";

        int pageNumber = 0;
        int pageSize = 4;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User>page = repository.findAllWithKeyword(keyword, pageable);

        List<User>users = page.getContent();

        users.forEach(System.out::println);

        assertThat(users.size()).isGreaterThan(0);
    }

    @Test
    public void testRandomStringGenerator(){
        String generatedString = RandomStringUtils.randomAlphabetic(10);

        System.out.println(generatedString);
    }
}
