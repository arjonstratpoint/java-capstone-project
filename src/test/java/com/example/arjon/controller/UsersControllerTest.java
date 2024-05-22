package com.example.arjon.controller;

import com.example.arjon.config.SecurityConfig;
import com.example.arjon.model.request.ChangePasswordRequest;
import com.example.arjon.model.request.ForgotPasswordValidateRequest;
import com.example.arjon.model.request.UserRequest;
import com.example.arjon.model.response.UserResponse;
import com.example.arjon.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static com.example.arjon.util.Constant.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasLength;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UsersControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private String userBaseUrl = "/api/user";

    @Test
    @Transactional
    void registrationSuccessful() throws Exception {
        UserRequest request = new UserRequest("newUser", "newUser01");
        this.mvc.perform(post(userBaseUrl+"/registration")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered"));
    }
    @Test
    void registrationErrorExistingUser() throws Exception {
        UserRequest request = new UserRequest("admin", "pwdadmin01");
        this.mvc.perform(post(userBaseUrl+"/registration")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(GENERIC_AUTH_ERROR_MESSAGE));
    }

    @Test
    void registrationErrorInvalidPassword() throws Exception {
        UserRequest request = new UserRequest("admin", "invalidpassword");
        this.mvc.perform(post(userBaseUrl+"/registration")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("password "+INVALID_PASSWORD_ERROR_MESSAGE));
    }

    @Test
    void loginSuccessfulAndGetUsersListTest() throws Exception {
        UserRequest request = new UserRequest("admin", "pwdadmin01");
        MvcResult result = this.mvc.perform(post(userBaseUrl+"/login")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        UserResponse reseponse = mapper.readValue(responseString, UserResponse.class);
        String token = reseponse.token();

        String list = this.mvc.perform(get(userBaseUrl+"/list")
                .header("Authorization","Bearer "+token))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        System.out.println("loginSuccessfulAndGetUsersListTest:result = "+list);
    }

    @Test
    void loginErrorInvalidUsernameOrPassword() throws Exception {
        UserRequest request = new UserRequest("invalid", "pwdinvalid01");
        this.mvc.perform(post(userBaseUrl+"/login")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(GENERIC_AUTH_ERROR_MESSAGE));
    }

    @Test
    @Transactional
    void forgotPasswordRequestSuccess() throws Exception {
        String user = "user";
        this.mvc.perform(post(userBaseUrl+"/forgot-password/request/"+user))
                .andExpect(status().isOk())
                .andExpect(content().string(hasLength(OTP_LENGTH)));
    }

    @Test
    @Transactional
    void forgotPasswordRequestInvalidCreds() throws Exception {
        String user = "invalidUser";
        this.mvc.perform(post(userBaseUrl+"/forgot-password/request/"+user))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(GENERIC_AUTH_ERROR_MESSAGE));
    }

    @Test
    @Transactional
    void forgotPasswordValidationSuccess() throws Exception {
        String user = "user";
        String code = this.mvc.perform(post(userBaseUrl+"/forgot-password/request/"+user))
                .andExpect(status().isOk())
                .andExpect(content().string(hasLength(OTP_LENGTH)))
                .andReturn().getResponse().getContentAsString();

        String newPassword = "userpassword01";
        ForgotPasswordValidateRequest request = new ForgotPasswordValidateRequest(code, newPassword);
        this.mvc.perform(post(userBaseUrl+"/forgot-password/validate/"+user)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(String.format(FORGOT_PASSWORD_SUCCESS_MESSAGE, user)));
    }

    @Test
    @Transactional
    void forgotPasswordValidationErrorInvalidCode() throws Exception {
        String user = "user";
        String newPassword = "userpassword01";
        ForgotPasswordValidateRequest request = new ForgotPasswordValidateRequest("invalidCode", newPassword);
        this.mvc.perform(post(userBaseUrl+"/forgot-password/validate/"+user)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(FORGOT_PASSWORD_ERROR_MESSAGE));
    }

    @Test
    @Transactional
    @WithMockUser(username = "user", password = "pwduser01", authorities = "SCOPE_USER")
    void changePasswordSuccess() throws Exception {
        String currentPassword = "pwduser01";
        String newPassword = "pwduser02";
        ChangePasswordRequest request = new ChangePasswordRequest(currentPassword, newPassword);
        this.mvc.perform(post(userBaseUrl+"/change-password")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(String.format(FORGOT_PASSWORD_SUCCESS_MESSAGE, "user")));
    }

    @Test
    @Transactional
    @WithMockUser(username = "user", password = "user", authorities = "SCOPE_USER")
    void changePasswordErrorInvalidCurrentPassword() throws Exception {
        String currentPassword = "wrongPassword01";
        String newPassword = "userpassword01";
        ChangePasswordRequest request = new ChangePasswordRequest(currentPassword, newPassword);
        this.mvc.perform(post(userBaseUrl+"/change-password")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(GENERIC_AUTH_ERROR_MESSAGE));
    }

    @Test
    @Transactional
    void changePasswordErrorUnauthorized() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("userpassword01", "userpassword01");
        this.mvc.perform(post(userBaseUrl+"/change-password")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}