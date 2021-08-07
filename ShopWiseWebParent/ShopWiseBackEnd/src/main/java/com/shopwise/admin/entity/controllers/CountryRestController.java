package com.shopwise.admin.entity.controllers;

import com.shopwise.admin.entity.repositories.CountryRepository;
import com.shopwise.common.entity.Country;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class CountryRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CountryRestController.class);

    @Autowired
    private CountryRepository repository;


    @GetMapping("/countries/list")
    public List<Country>listAll(){
        return repository.findAllByOrderByNameAsc();
    }

    @PostMapping("/countries/save")
    public String save(@RequestBody Country country){
        LOGGER.info("CountryRestController | save | Country : " + country.toString());

        Country savedCountry = repository.save(country);
        return String.valueOf(savedCountry.getId());
    }

    @DeleteMapping("/countries/delete/{id}")
    public void delete(@PathVariable("id") Integer id){
        repository.deleteById(id);
    }

    @PostMapping("/countries/check_uniqueness")
    @ResponseBody
    public String checkUniqueness(@RequestBody Map<String,String> data){
        String name = data.get("name");

        LOGGER.info("CountryRestController | checkUniqueness | name : " + name);

        Country countryByName = repository.findByName(name);

        LOGGER.info("CountryRestController | findByName | name : " + countryByName);

        if (countryByName != null) {
            return "Duplicate";
        }

        return "OK";
    }
}
