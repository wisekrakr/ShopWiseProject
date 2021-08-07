package com.shopwise.admin.entity.controllers.helpers;

import com.shopwise.admin.entity.repositories.CurrencyRepository;
import com.shopwise.admin.entity.services.SettingService;
import com.shopwise.admin.processes.FileUpload;
import com.shopwise.admin.utils.GeneralSettingsContext;
import com.shopwise.common.entity.Currency;
import com.shopwise.common.entity.Setting;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class SettingsHelper {

    public static void saveSiteLogo(MultipartFile multipartFile, GeneralSettingsContext context) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            String value = "/site-logo/" + fileName;
            context.updateSiteLogo(value);

            String uploadDir = "./ShopWiseWebParent/site-logo/";
            FileUpload.cleanDir(uploadDir);
            FileUpload.saveFile(uploadDir, fileName, multipartFile);
        }
    }

    public static void saveCurrencySymbol(HttpServletRequest request, GeneralSettingsContext context, CurrencyRepository currencyRepository) {
        Integer currencyId = Integer.parseInt(request.getParameter("CURRENCY_ID"));
        Optional<Currency> findByIdResult = currencyRepository.findById(currencyId);

        if (findByIdResult.isPresent()) {
            Currency currency = findByIdResult.get();
            context.updateCurrencySymbol(currency.getSymbol());
        }
    }

    public static void updateSettingValuesFromForm(HttpServletRequest request, List<Setting> listSettings, SettingService service) {
        for (Setting setting : listSettings) {
            String value = request.getParameter(setting.getKey());
            if (value != null) {
                setting.setValue(value);
            }
        }

        service.saveAll(listSettings);
    }
}
