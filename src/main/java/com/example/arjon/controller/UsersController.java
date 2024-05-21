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
    private final ForgotPasswordRepository forgotPasswordRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public UsersController(UserRepository userRepository, ForgotPasswordRepository forgotPasswordRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, TokenService tokenService) {
        this.userRepository = userRepository;
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
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
        Optional<Users> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            Integer userId = optionalUser.get().id();
            List<ForgotPassword> forgotPasswordList = forgotPasswordRepository.findByUserId(userId);
            // Update all forgot password request of user to is_valid false
            List<ForgotPassword> updatedForgotPasswordList = new ArrayList<>(forgotPasswordList.stream()
                    .map(ForgotPassword::new)
                    .toList());
            String otp = OTPForgotPassword.generateOTP();
            ForgotPassword forgotPassword = new ForgotPassword(userId, otp);
            updatedForgotPasswordList.add(forgotPassword);
            forgotPasswordRepository.saveAll(updatedForgotPasswordList);
            return ResponseEntity.ok(otp);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(GENERIC_AUTH_ERROR_MESSAGE);
    }

    // Validates the code for forgot password
    @PostMapping("/forgot-password/validate/{username}")
    public ResponseEntity<String> passwordResetValidate(@PathVariable String username, @RequestBody ForgotPasswordValidateRequest request) {
        Optional<Users> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();
            Optional<ForgotPassword> optionalForgotPassword = forgotPasswordRepository.findByIdAndCode(user.id(), request.code());
            if (optionalForgotPassword.isPresent()) {
                //Update ForgotPassword table
                ForgotPassword fp = optionalForgotPassword.get();
                ForgotPassword forgotPassword = new ForgotPassword(fp);
                forgotPasswordRepository.save(forgotPassword);

                //Update User tables password
                updateUserPassword(user, request.password());
                return ResponseEntity.ok(String.format(FORGOT_PASSWORD_SUCCESS_MESSAGE, user.username()));
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(FORGOT_PASSWORD_ERROR_MESSAGE);
    }

    // Change users password
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Authentication currentAuthentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authentication.getName(), request.currentPassword()));
        if (currentAuthentication.isAuthenticated()) {
            Optional<Users> optionalUser = userRepository.findByUsername(authentication.getName());
            if (optionalUser.isPresent()) {
                Users user = optionalUser.get();
                updateUserPassword(user, request.newPassword());
                return ResponseEntity.ok(String.format(FORGOT_PASSWORD_SUCCESS_MESSAGE, user.username()));
            }
        }
        // Generic error message for security
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(FORGOT_PASSWORD_ERROR_MESSAGE);
    }

    // Get all users list
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @GetMapping("/list")
    public Iterable<Users> usersList() {
        return userRepository.findAll();
    }

    private void updateUserPassword(Users user, String password) {
        String encryptedPassword = passwordEncoder.encode(password);
        Users updatedPasswordUser = new Users(user.id(), user.username(), encryptedPassword, user.role(), user.dateCreated());
        userRepository.save(updatedPasswordUser);
    }
}
