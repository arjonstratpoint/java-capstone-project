package com.example.arjon.controller;

import com.example.arjon.model.Users;
import com.example.arjon.model.request.UserRequest;
import com.example.arjon.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UsersController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UsersController(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<Users> login(@RequestBody UserRequest userRequest) {
        Optional<Users> optionalUsers = userRepository.findByUsername(userRequest.username());
        if (optionalUsers.isPresent()) {
            Users user = optionalUsers.get();
            if (bCryptPasswordEncoder.matches(userRequest.password(), user.password())) {
                return ResponseEntity.ok(user);
            }else {
                return ResponseEntity.noContent().build();
            }
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/registration")
    public void registration(@RequestBody UserRequest userRequest) {
        String encryptedPassword = bCryptPasswordEncoder.encode(userRequest.password());
        Users users = new Users(userRequest.username(), encryptedPassword);
        userRepository.save(users);
    }
}
