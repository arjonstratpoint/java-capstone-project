package com.example.arjon.controller;

import com.example.arjon.model.Users;
import com.example.arjon.model.request.UserRequest;
import com.example.arjon.repository.UserRepository;
import com.example.arjon.service.TokenService;
import com.example.arjon.util.PasswordSecurity;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UsersController {

    private final UserRepository userRepository;
    private final PasswordSecurity bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public UsersController(UserRepository userRepository, PasswordSecurity bCryptPasswordEncoder, AuthenticationManager authenticationManager, TokenService tokenService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public String login(@RequestBody UserRequest userRequest) {
        Optional<Users> optionalUsers = userRepository.findByUsername(userRequest.username());
        if (optionalUsers.isPresent()) {
            try {
                Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userRequest.username(), userRequest.password()));
                return tokenService.generateToken(authentication);
            } catch (BadCredentialsException ignored) {}
        }
        // Generic error message for security
        return "Invalid username or password";

    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/registration")
    public void registration(@RequestBody UserRequest userRequest) {
        String encryptedPassword = bCryptPasswordEncoder.encode(userRequest.password());
        Users users = new Users(userRequest.username(), encryptedPassword);
        userRepository.save(users);
    }
}
