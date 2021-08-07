package com.shopwise.common.services;

import com.shopwise.common.entity.Setting;
import com.shopwise.common.entity.SettingCategory;
import com.shopwise.common.repository.SettingRepository;
import com.shopwise.common.services.impl.ISettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingService implements ISettingService {

    @Autowired
    private SettingRepository repository;

    @Override
    public List<Setting> getGeneralSettings() {
        return repository.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
    }


}
