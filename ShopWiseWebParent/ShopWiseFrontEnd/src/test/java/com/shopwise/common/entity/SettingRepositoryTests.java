package com.shopwise.common.entity;

import com.shopwise.common.repository.SettingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class SettingRepositoryTests {

    @Autowired
    private SettingRepository repository;

    @Test
    public void testFindByTwoCategories(){
        List<Setting> settings = repository.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);

        settings.forEach(System.out::println);


    }
}
