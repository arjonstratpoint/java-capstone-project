package com.example.arjon.controller;

import com.example.arjon.model.Users;
import com.example.arjon.model.request.UserRequest;
import com.example.arjon.model.response.UserResponse;
import com.example.arjon.repository.UserRepository;
import com.example.arjon.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UsersController {

    private final UserRepository userRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public UsersController(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody UserRequest userRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userRequest.username(), userRequest.password()));
            String token = tokenService.generateToken(authentication);
            String role = authentication.getAuthorities().stream().findFirst().get().toString();
            UserResponse response = new UserResponse(authentication.getName(), role, token);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException ignored) {}
        // Generic error message for security
        return ResponseEntity.notFound().build();

    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/registration")
    public void registration(@RequestBody UserRequest userRequest) {
        String encryptedPassword = passwordEncoder.encode(userRequest.password());
        Users users = new Users(userRequest.username(), encryptedPassword);
        userRepository.save(users);
    }
}
