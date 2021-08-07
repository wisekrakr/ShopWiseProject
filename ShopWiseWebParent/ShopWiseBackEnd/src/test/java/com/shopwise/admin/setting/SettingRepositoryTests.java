package com.shopwise.admin.setting;

import com.shopwise.admin.entity.repositories.SettingRepository;
import com.shopwise.common.entity.Setting;
import com.shopwise.common.entity.SettingCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest //disable full auto-configuration and instead apply only configuration relevant to JPA tests
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // test against a real database
@Rollback(false) // keep the data committed in the real database
public class SettingRepositoryTests {

    @Autowired
    private SettingRepository repository;

    @Test
    public void testCreateGeneralSetting(){
        Setting siteName = new Setting("SITE_NAME", "ShopWise", SettingCategory.GENERAL);
        repository.save(siteName);
//        Setting siteLogo = new Setting("SITE_LOGO", "ShopWise.png", SettingCategory.GENERAL);
//        Setting copyright = new Setting("COPYRIGHT", "Copyright © ShopWise 2021. Wisekrakr Ltd.", SettingCategory.GENERAL);
//
//        repository.saveAll(List.of(siteLogo,copyright));
//
//        Iterable<Setting> all = repository.findAll();
//
//        assertThat(all).size().isGreaterThan(0);
    }

    @Test
    public void testCreateCurrencySettings() {
        Setting currencyId = new Setting("CURRENCY_ID", "1", SettingCategory.CURRENCY);
        Setting symbol = new Setting("CURRENCY_SYMBOL", "€", SettingCategory.CURRENCY);
        Setting symbolPosition = new Setting("CURRENCY_SYMBOL_POSITION", "before", SettingCategory.CURRENCY);
        Setting decimalPointType = new Setting("DECIMAL_POINT_TYPE", "POINT", SettingCategory.CURRENCY);
        Setting decimalDigits = new Setting("DECIMAL_DIGITS", "2", SettingCategory.CURRENCY);
        Setting thousandsPointType = new Setting("THOUSANDS_POINT_TYPE", "COMMA", SettingCategory.CURRENCY);

        repository.saveAll(List.of(currencyId, symbol, symbolPosition, decimalPointType,
                decimalDigits, thousandsPointType));

    }

//    @Test
//    public void testListSettingsByCategory() {
//        List<Setting> settings = repository.findByCategory(SettingCategory.GENERAL);
//
//        settings.forEach(System.out::println);
//    }
}
