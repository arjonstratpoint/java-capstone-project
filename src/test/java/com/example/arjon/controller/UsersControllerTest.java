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

    private static final Logger log = LoggerFactory.getLogger(UsersControllerTest.class);
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private String userBaseUrl = "/api/user";

    @Test
    @Transactional
    void registrationSuccessful() throws Exception {
        UserRequest request = new UserRequest("newUser", "newUser");
        this.mvc.perform(post(userBaseUrl+"/registration")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered"));
    }
    @Test
    void registrationErrorExistingUser() throws Exception {
        UserRequest request = new UserRequest("admin", "admin");
        this.mvc.perform(post(userBaseUrl+"/registration")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(GENERIC_AUTH_ERROR_MESSAGE));
    }

    @Test
    void loginSuccessfulAndGetUsersListTest() throws Exception {
        UserRequest request = new UserRequest("admin", "admin");
        MvcResult result = this.mvc.perform(post(userBaseUrl+"/login")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        UserResponse reseponse = mapper.readValue(responseString, UserResponse.class);
        String token = reseponse.token();

        this.mvc.perform(get(userBaseUrl+"/list")
                .header("Authorization","Bearer "+token))
                .andExpect(status().isOk());
    }

    @Test
    void loginErrorInvalidUsernameOrPassword() throws Exception {
        UserRequest request = new UserRequest("invalid", "invalid");
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

        String newPassword = "newPassword";
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
        String newPassword = "newPassword";
        ForgotPasswordValidateRequest request = new ForgotPasswordValidateRequest("invalidCode", newPassword);
        this.mvc.perform(post(userBaseUrl+"/forgot-password/validate/"+user)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(FORGOT_PASSWORD_ERROR_MESSAGE));
    }

    @Test
    @Transactional
    @WithMockUser(username = "user", password = "user", authorities = "SCOPE_USER")
    void changePasswordSuccess() throws Exception {
        String currentPassword = "user";
        String newPassword = "newPassword";
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
        String currentPassword = "wrongPassword";
        String newPassword = "newPassword";
        ChangePasswordRequest request = new ChangePasswordRequest(currentPassword, newPassword);
        this.mvc.perform(post(userBaseUrl+"/change-password")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(FORGOT_PASSWORD_ERROR_MESSAGE));
    }
}