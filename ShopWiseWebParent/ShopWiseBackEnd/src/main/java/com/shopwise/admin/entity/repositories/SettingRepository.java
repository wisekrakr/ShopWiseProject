package com.shopwise.admin.entity.repositories;

import com.shopwise.common.entity.Setting;
import com.shopwise.common.entity.SettingCategory;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SettingRepository extends CrudRepository<Setting, String> {

    List<Setting>findByCategory(SettingCategory category);
}
