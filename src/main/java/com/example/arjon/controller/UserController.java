package com.example.arjon.controller;

import com.example.arjon.model.User;
import com.example.arjon.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/registration")
    public void registration(@RequestBody User user) {
        userRepository.save(user);
    }
}
