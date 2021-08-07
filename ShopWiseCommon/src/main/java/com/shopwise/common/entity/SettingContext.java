package com.shopwise.common.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class SettingContext implements Serializable {

    @Serial
    private static final long serialVersionUID = 4897816450808156113L;

    private final List<Setting> settingsList;

    public SettingContext(List<Setting> settingsList) {
        this.settingsList = settingsList;
    }

    public Setting get(String key) {
        int index = settingsList.indexOf(new Setting(key));
        if (index >= 0) {
            return settingsList.get(index);
        }

        return null;
    }

    public String getValue(String key) {
        Setting setting = get(key);
        if (setting != null) {
            return setting.getValue();
        }

        return null;
    }

    public void update(String key, String value) {
        Setting setting = get(key);
        if (setting != null && value != null) {
            setting.setValue(value);
        }
    }

    public List<Setting> list() {
        return settingsList;
    }
}
