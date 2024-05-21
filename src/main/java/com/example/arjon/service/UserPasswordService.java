package com.example.arjon.service;

import com.example.arjon.model.ForgotPassword;
import com.example.arjon.model.Users;
import com.example.arjon.model.request.ForgotPasswordValidateRequest;
import com.example.arjon.repository.ForgotPasswordRepository;
import com.example.arjon.repository.UserRepository;
import com.example.arjon.util.OTPForgotPassword;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserPasswordService {

    private final ForgotPasswordRepository forgotPasswordRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserPasswordService(ForgotPasswordRepository forgotPasswordRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String forgotPasswordRequest(Integer userId) {
        List<ForgotPassword> forgotPasswordList = forgotPasswordRepository.findByUserId(userId);
        // Update all forgot password request of user to is_valid false
        List<ForgotPassword> updatedForgotPasswordList = new ArrayList<>(forgotPasswordList.stream()
                .map(ForgotPassword::new)
                .toList());
        String otp = OTPForgotPassword.generateOTP();
        ForgotPassword forgotPassword = new ForgotPassword(userId, otp);
        updatedForgotPasswordList.add(forgotPassword);
        forgotPasswordRepository.saveAll(updatedForgotPasswordList);
        return otp;
    }

    public Boolean forgotPasswordValidation(Users user, ForgotPasswordValidateRequest request) {
        Optional<ForgotPassword> optionalForgotPassword = forgotPasswordRepository.findByIdAndCode(user.id(), request.code());
        if (optionalForgotPassword.isPresent()) {
            //Update ForgotPassword table
            ForgotPassword fp = optionalForgotPassword.get();
            ForgotPassword forgotPassword = new ForgotPassword(fp);
            forgotPasswordRepository.save(forgotPassword);

            //Update User tables password
            updateUserPassword(user, request.password());
            return true;
        }
        return false;
    }

    public void updateUserPassword(Users user, String password) {
        String encryptedPassword = passwordEncoder.encode(password);
        Users updatedPasswordUser = new Users(user.id(), user.username(), encryptedPassword, user.role(), user.dateCreated());
        userRepository.save(updatedPasswordUser);
    }

}
