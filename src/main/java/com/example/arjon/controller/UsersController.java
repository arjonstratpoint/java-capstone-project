package com.example.arjon.controller;

import com.example.arjon.model.ForgotPassword;
import com.example.arjon.model.Users;
import com.example.arjon.model.request.ChangePasswordRequest;
import com.example.arjon.model.request.ForgotPasswordValidateRequest;
import com.example.arjon.model.request.UserRequest;
import com.example.arjon.model.response.UserResponse;
import com.example.arjon.repository.ForgotPasswordRepository;
import com.example.arjon.repository.UserRepository;
import com.example.arjon.service.TokenService;
import com.example.arjon.service.UserPasswordService;
import com.example.arjon.util.OTPForgotPassword;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import static com.example.arjon.util.Constant.*;

/**
 * REST API for users
 *
 * All users endpoints are publicly open
 * except the change-password and users-list, annotated by @PreAuthorize
 */
@RestController
@RequestMapping("/api/user")
public class UsersController {

    private final UserRepository userRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserPasswordService userPasswordService;

    public UsersController(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, TokenService tokenService, UserPasswordService userPasswordService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userPasswordService = userPasswordService;
    }

    // Logs in a registered user
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody UserRequest userRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userRequest.username(), userRequest.password()));
        String token = tokenService.generateToken(authentication);
        String role = authentication.getAuthorities().stream().findFirst().get().toString();
        UserResponse response = new UserResponse(authentication.getName(), role, token);
        return ResponseEntity.ok(response);
    }

    // Register a new user
    @PostMapping("/registration")
    public ResponseEntity<String> registration(@Valid @RequestBody UserRequest userRequest) {
        String encryptedPassword = passwordEncoder.encode(userRequest.password());
        Users users = new Users(userRequest.username(), encryptedPassword);
        userRepository.save(users);
        return ResponseEntity.ok("User registered");
    }

    // Request a code for forgot password
    @PostMapping("/forgot-password/request/{username}")
    public ResponseEntity<String> passwordResetRequest(@PathVariable String username) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    Integer userId = user.id();
                    String otp = userPasswordService.forgotPasswordRequest(userId);
                    return ResponseEntity.ok(otp);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(GENERIC_AUTH_ERROR_MESSAGE));
    }

    // Validates the code for forgot password
    @PostMapping("/forgot-password/validate/{username}")
    public ResponseEntity<String> passwordResetValidate(@PathVariable String username, @RequestBody ForgotPasswordValidateRequest request) {
        return userRepository.findByUsername(username)
                .filter(user -> userPasswordService.forgotPasswordValidation(user, request))
                .map(user -> ResponseEntity.ok(String.format(FORGOT_PASSWORD_SUCCESS_MESSAGE, user.username())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(FORGOT_PASSWORD_ERROR_MESSAGE));
    }

    // Change users password
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Authentication currentAuthentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authentication.getName(), request.currentPassword()));
        return userRepository.findByUsername(authentication.getName())
                .filter(user -> currentAuthentication.isAuthenticated())
                .map(user -> {
                    userPasswordService.updateUserPassword(user, request.newPassword());
                    return ResponseEntity.ok(String.format(FORGOT_PASSWORD_SUCCESS_MESSAGE, user.username()));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(FORGOT_PASSWORD_ERROR_MESSAGE));
    }

    // Get all users list
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @GetMapping("/list")
    public Iterable<Users> usersList() {
        return userRepository.findAll();
    }
}
