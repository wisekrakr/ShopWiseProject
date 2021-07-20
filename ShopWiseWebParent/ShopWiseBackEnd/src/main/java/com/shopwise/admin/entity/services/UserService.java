package com.shopwise.admin.entity.services;

import com.shopwise.admin.entity.repositories.RoleRepository;
import com.shopwise.admin.entity.repositories.UserRepository;
import com.shopwise.admin.entity.services.impl.IUserService;
import com.shopwise.admin.utils.UserNotFoundException;
import com.shopwise.common.entity.Role;
import com.shopwise.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * UserService holds all the "Actions" for users
 */

@Service
@Transactional
public class UserService implements IUserService {

    public static final int USERS_PER_PAGE = 6;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAll(){
        return (List<User>) userRepository.findAll(Sort.by("lastName").ascending());
    }

    @Override
    public Page<User> getPerPage(int pageNumber, String sortField, String sortDirection, String keyword){
        Sort sort = Sort.by(sortField);
        sort = sortDirection.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNumber -1, USERS_PER_PAGE, sort);

        if(keyword != null){
            return userRepository.findAllWithKeyword(keyword, pageable);
        }

        return userRepository.findAll(pageable);
    }

    @Override
    public List<Role> getRoles() {
        return (List<Role>) roleRepository.findAll();
    }

    @Override
    public User save(User user) {

        if(user.getId() != null){
            //if updating
            User existingUser = userRepository.findById(user.getId()).get();
            if(user.getPassword().isEmpty()){
                //admin wants to keep user's password
                user.setPassword(existingUser.getPassword());
            }else{
                //changing password
                encodePassword(user);
            }
        }else{
            //if user is new
            encodePassword(user);
        }

        return userRepository.save(user);
    }

    @Override
    public User updateAccount(User user){
        User userInDB = userRepository.findById(user.getId()).get();

        if(!user.getPassword().isEmpty()){
            userInDB.setPassword(user.getPassword());
            encodePassword(userInDB);
        }

        if(user.getAvatar() != null){
            userInDB.setAvatar(user.getAvatar());
        }

        userInDB.setFirstName(user.getFirstName());
        userInDB.setLastName(user.getLastName());
        userInDB.setPhoneNumber(user.getPhoneNumber());
        userInDB.setEnabled(user.isEnabled());

        return userRepository.save(userInDB);
    }

    @Override
    public User getById(Integer id) throws UserNotFoundException {
        try {
            return userRepository.findById(id).get();

        }catch (NoSuchElementException e){
            throw new UserNotFoundException("Could not find any user with ID: " + id);
        }
    }

    @Override
    public User getByEmail(String email) {
            return userRepository.findByEmail(email);
    }

    @Override
    public void delete(Integer id) throws UserNotFoundException {

        Long countById = userRepository.countById(id);

        if(countById == null || countById == 0){
            throw new UserNotFoundException("Could not find any user with ID: " + id);
        }

        userRepository.deleteById(id);
    }

    private void encodePassword(User user){
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }

    @Override
    public boolean isEmailUnique(Integer id,String email){
        User user = userRepository.findByEmail(email);

        if(user == null) return true;

        if(id == null){
            return false;
        }else{
            return user.getId().equals(id);
        }
    }

    @Override
    public void updateEnabledStatus(Integer id, boolean enabled){
        userRepository.toggleEnabledStatus(id, enabled);
    }
}
