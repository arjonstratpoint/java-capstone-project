package com.example.arjon.util;

import com.example.arjon.model.Users;
import com.example.arjon.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthenticationFacadeImpl implements AuthenticationFacade{

    @Autowired
    public UserRepository userRepository;

    @Override
    public Integer getUserIdFromAuthentication() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Users> optionalUser = userRepository.findByUsername(userName);
        return optionalUser.map(Users::id).orElse(null);
    }
}
