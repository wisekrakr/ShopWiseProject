package com.shopwise.admin.entity.services.impl;

import com.shopwise.admin.utils.UserNotFoundException;
import com.shopwise.common.entity.Role;
import com.shopwise.common.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IUserService {
    List<User> getAll();

    List<Role> getRoles();

    User save(User user);

    boolean isEmailUnique(Integer id, String email);

    User getById(Integer id) throws UserNotFoundException;

    void delete(Integer id) throws UserNotFoundException;

    void updateEnabledStatus(Integer id, boolean enabled);

    Page<User> getPerPage(int pageNumber, String sortField, String sortDirection, String keyword);

    User getByEmail(String email);

    User updateAccount(User userInForm);
}
