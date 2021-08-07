package com.shopwise.admin.entity.controllers;

import com.shopwise.admin.entity.repositories.StateRepository;
import com.shopwise.common.entity.Country;
import com.shopwise.common.entity.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class StateRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StateRestController.class);

    @Autowired
    private StateRepository repository;

    @GetMapping("/states/by_country/{id}")
    public List<StateDTO> listByCountry(@PathVariable("id") Integer id) {
        List<State> stateList = repository.findByCountryOrderByNameAsc(new Country(id));
        List<StateDTO> stateDTOList = new ArrayList<>();

        for (State state: stateList){
            stateDTOList.add(new StateDTO(state.getId(), state.getName()));
        }

        return stateDTOList;
    }

    @PostMapping("/states/save")
    public String save(@RequestBody State state) {
        LOGGER.info("StateRestController | save | State : " + state.toString());

        State savedState = repository.save(state);
        return String.valueOf(savedState.getId());
    }

    @DeleteMapping("/states/delete/{id}")
    public void delete(@PathVariable("id") Integer id) {
        repository.deleteById(id);
    }

    @PostMapping("/states/check_uniqueness")
    @ResponseBody
    public String checkUniqueness(@RequestBody Map<String,String> data){
        String name = data.get("name");

        LOGGER.info("StateRestController | checkUniqueness | name : " + name);

        State state = repository.findByName(name);

        LOGGER.info("StateRestController | findByName | name : " + state);

        if (state != null) {
            return "Duplicate";
        }
        return "OK";
    }
}
