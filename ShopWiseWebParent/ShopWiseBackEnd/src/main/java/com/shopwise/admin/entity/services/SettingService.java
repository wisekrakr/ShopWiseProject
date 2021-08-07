package com.shopwise.admin.entity.services;

import com.shopwise.admin.entity.repositories.SettingRepository;
import com.shopwise.admin.entity.services.impl.ISettingService;
import com.shopwise.admin.utils.GeneralSettingsContext;
import com.shopwise.common.entity.Setting;
import com.shopwise.common.entity.SettingCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SettingService implements ISettingService {

    @Autowired
    private SettingRepository repository;

    @Override
    public List<Setting> listAll() {
        return (List<Setting>) repository.findAll();
    }

    @Override
    public GeneralSettingsContext getGeneralSettings() {
        List<Setting>settings = new ArrayList<>();

        List<Setting> generalSettings = repository.findByCategory(SettingCategory.GENERAL);
        List<Setting> currencySettings = repository.findByCategory(SettingCategory.CURRENCY);

        settings.addAll(generalSettings);
        settings.addAll(currencySettings);

        return new GeneralSettingsContext(settings);
    }

    @Override
    public void saveAll(Iterable<Setting> settings) {
        repository.saveAll(settings);
    }

    @Override
    public List<Setting> getMailServerSettings() {
        return repository.findByCategory(SettingCategory.MAIL_SERVER);
    }

    @Override
    public List<Setting> getMailTemplateSettings() {
        return repository.findByCategory(SettingCategory.MAIL_TEMPLATES);
    }
}
