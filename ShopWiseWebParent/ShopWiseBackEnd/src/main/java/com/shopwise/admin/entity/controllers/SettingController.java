package com.shopwise.admin.entity.controllers;

import com.shopwise.admin.entity.controllers.helpers.SettingsHelper;
import com.shopwise.admin.entity.repositories.CurrencyRepository;
import com.shopwise.admin.entity.services.SettingService;
import com.shopwise.admin.utils.GeneralSettingsContext;
import com.shopwise.common.entity.Currency;
import com.shopwise.common.entity.Setting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Controller
public class SettingController {

    @Autowired
    private SettingService service;

    @Autowired
    private CurrencyRepository currencyRepository;

    @GetMapping("/settings")
    public String listAll(Model model){

        List<Setting> settings = service.listAll();
        List<Currency> currenciesByOrderByNameAsc = currencyRepository.findAllByOrderByNameAsc();

        settings.forEach(setting -> model.addAttribute(setting.getKey(), setting.getValue()));
        model.addAttribute("currencies", currenciesByOrderByNameAsc);
        model.addAttribute("editMode", false);
        model.addAttribute("pageTitle", "Settings");

        return "settings/settings";
    }

    @PostMapping("/settings/save_general")
    public String saveGeneralSettings(@RequestParam("fileImage") MultipartFile multipartFile,
                                      HttpServletRequest request, RedirectAttributes redirectAttributes) throws IOException {
        GeneralSettingsContext generalSettings = service.getGeneralSettings();

        SettingsHelper.saveSiteLogo(multipartFile, generalSettings);
        SettingsHelper.saveCurrencySymbol(request, generalSettings,currencyRepository);

        SettingsHelper.updateSettingValuesFromForm(request, generalSettings.list(),service);

        redirectAttributes.addFlashAttribute("message", "General settings have been saved.");

        return "redirect:/settings";
    }

    @PostMapping("/settings/save_mail_server")
    public String saveMailServerSettings(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        List<Setting> mailServerSettings = service.getMailServerSettings();
        SettingsHelper.updateSettingValuesFromForm(request, mailServerSettings,service);

        redirectAttributes.addFlashAttribute("message", "Mail server settings have been saved");

        return "redirect:/settings#mailServer";
    }

    @PostMapping("/settings/save_mail_templates")
    public String saveMailTemplateSettings(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        List<Setting> mailTemplateSettings = service.getMailTemplateSettings();
        SettingsHelper.updateSettingValuesFromForm(request, mailTemplateSettings,service);

        redirectAttributes.addFlashAttribute("message", "Mail template settings have been saved");

        return "redirect:/settings#mailTemplates";
    }
}
