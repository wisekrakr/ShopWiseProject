package com.shopwise.admin.security;

import com.shopwise.admin.entity.repositories.UserRepository;
import com.shopwise.admin.security.UserAccountDetails;
import com.shopwise.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserAuthenticationService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = repository.findByEmail(email);

        if(user != null){
            return new UserAccountDetails(user);
        }
        throw new UsernameNotFoundException("Could not find user with e-mail: " + email);

    }
}
