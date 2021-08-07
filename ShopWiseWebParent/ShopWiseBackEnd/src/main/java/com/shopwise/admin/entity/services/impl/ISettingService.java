package com.shopwise.admin.entity.services.impl;

import com.shopwise.admin.utils.GeneralSettingsContext;
import com.shopwise.common.entity.Setting;

import java.util.List;

public interface ISettingService {

    List<Setting>listAll();
    GeneralSettingsContext getGeneralSettings();
    void saveAll(Iterable<Setting>settings);
    List<Setting> getMailServerSettings();
    List<Setting> getMailTemplateSettings();
}
