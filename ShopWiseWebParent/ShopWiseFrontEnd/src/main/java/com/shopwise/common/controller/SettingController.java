package com.shopwise.common.controller;

import com.shopwise.common.controller.utils.JsonConverter;
import com.shopwise.common.services.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/")
public class SettingController {

    @Autowired
    private SettingService service;

    @GetMapping("/settings")
    public String getSettings(){
        return JsonConverter.convert(service.getGeneralSettings());
    }
}
