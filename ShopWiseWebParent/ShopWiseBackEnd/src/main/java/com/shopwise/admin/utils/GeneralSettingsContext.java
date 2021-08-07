package com.shopwise.admin.utils;

import com.shopwise.common.entity.Setting;
import com.shopwise.common.entity.SettingContext;

import java.io.Serial;
import java.util.List;

public class GeneralSettingsContext extends SettingContext {

    @Serial
    private static final long serialVersionUID = -6832147723128442180L;

    public GeneralSettingsContext(List<Setting> settingsList) {
        super(settingsList);
    }

    public void updateCurrencySymbol(String value) {
        super.update("CURRENCY_SYMBOL", value);
    }

    public void updateSiteLogo(String value) {
        super.update("SITE_LOGO", value);
    }
}
