package com.locassa.yamo.service;

import com.locassa.yamo.model.User;
import com.locassa.yamo.model.enums.UserType;
import com.locassa.yamo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class YamoUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByEmailAndEnabledTrue(username);

        if (null == user) {
            throw new RuntimeException("Username not found.");
        }

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

        switch (user.getUserType()) {
            case GLOBAL:
                authorities.add(new SimpleGrantedAuthority("ROLE_GLOBAL"));
            case ADMIN:
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            case USER:
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            case GUEST:
                authorities.add(new SimpleGrantedAuthority("ROLE_GUEST"));
        }

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);

    }

}
