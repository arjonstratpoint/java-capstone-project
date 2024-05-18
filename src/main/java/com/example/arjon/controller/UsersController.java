package com.example.arjon.controller;

import com.example.arjon.model.ForgotPassword;
import com.example.arjon.model.Users;
import com.example.arjon.model.request.ChangePasswordRequest;
import com.example.arjon.model.request.ForgotPasswordValidateRequest;
import com.example.arjon.model.request.UserRequest;
import com.example.arjon.model.response.ErrorResponse;
import com.example.arjon.model.response.UserResponse;
import com.example.arjon.repository.ForgotPasswordRepository;
import com.example.arjon.repository.UserRepository;
import com.example.arjon.service.TokenService;
import com.example.arjon.util.OTPForgotPassword;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody UserRequest userRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userRequest.username(), userRequest.password()));
        String token = tokenService.generateToken(authentication);
        String role = authentication.getAuthorities().stream().findFirst().get().toString();
        UserResponse response = new UserResponse(authentication.getName(), role, token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/registration")
    public ResponseEntity<String> registration(@Valid @RequestBody UserRequest userRequest) {
        String encryptedPassword = passwordEncoder.encode(userRequest.password());
        Users users = new Users(userRequest.username(), encryptedPassword);
        userRepository.save(users);
        return ResponseEntity.ok("User registered");
    }

    @PostMapping("/forgot-password/request/{username}")
    public String passwordResetRequest(@PathVariable String username) {
        Optional<Users> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            Integer userId = optionalUser.get().id();
            List<ForgotPassword> forgotPasswordList = forgotPasswordRepository.findByUserId(userId);
            // Update all forgot password request of user to is_valid false
            List<ForgotPassword> updatedForgotPasswordList = new ArrayList<>(forgotPasswordList.stream()
                    .map(fp -> new ForgotPassword(fp.id(), fp.userId(), fp.code(), false, fp.dateCreated()))
                    .toList());
            String otp = OTPForgotPassword.generateOTP();
            ForgotPassword forgotPassword = new ForgotPassword(userId, otp);
            updatedForgotPasswordList.add(forgotPassword);
            forgotPasswordRepository.saveAll(updatedForgotPasswordList);
            return otp;
        }
        return "Generic Error";
    }

    @PostMapping("/forgot-password/validate/{username}")
    public String passwordResetValidate(@PathVariable String username, @RequestBody ForgotPasswordValidateRequest request) {
        Optional<Users> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();
            Optional<ForgotPassword> optionalForgotPassword = forgotPasswordRepository.findByIdAndCode(user.id(), request.code());
            if (optionalForgotPassword.isPresent()) {
                //Update ForgotPassword table
                ForgotPassword fp = optionalForgotPassword.get();
                ForgotPassword forgotPassword = new ForgotPassword(fp.id(), fp.userId(), fp.code(), false, fp.dateCreated());
                forgotPasswordRepository.save(forgotPassword);

                //Update User tables password
                updateUserPassword(user, request.password());
                return "Password Changed for : "+ user.username();
            }
        }
        return "Invalid Code";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/change-password")
    public String changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Authentication currentAuthentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authentication.getName(), request.currentPassword()));
            if (currentAuthentication.isAuthenticated()) {
                Optional<Users> optionalUser = userRepository.findByUsername(authentication.getName());
                if (optionalUser.isPresent()) {
                    Users user = optionalUser.get();
                    updateUserPassword(user, request.newPassword());
                    return "Password Changed for : "+ user.username();
                }
            }
        } catch (BadCredentialsException ignored) {}
        // Generic error message for security
        return "Invalid Credentials";
    }

    private void updateUserPassword(Users user, String password) {
        String encryptedPassword = passwordEncoder.encode(password);
        Users updatedPasswordUser = new Users(user.id(), user.username(), encryptedPassword, user.role(), user.dateCreated());
        userRepository.save(updatedPasswordUser);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleValidationExceptions(MethodArgumentNotValidException ex) {
        FieldError error = (FieldError) ex.getBindingResult().getAllErrors().stream().findFirst().get();
        String fieldName = error.getField();
        String errorMessage = error.getDefaultMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(fieldName +" "+ errorMessage));
    }

    @ExceptionHandler({DataIntegrityViolationException.class, BadCredentialsException.class})
    public ResponseEntity handleValidationExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Invalid username or password"));
    }
}
