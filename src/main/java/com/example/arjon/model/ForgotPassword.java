package com.example.arjon.model;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

public record ForgotPassword(
        @Id
        Integer id,
        Integer userId,
        String code,
        Boolean isValid,
        LocalDateTime dateCreated
) {
    public ForgotPassword(Integer userId, String code) {
        this(null, userId, code, true, LocalDateTime.now());
    }

    public ForgotPassword(Integer id, Integer userId, String code, Boolean isValid, LocalDateTime dateCreated) {
        this.id = id;
        this.userId = userId;
        this.code = code;
        this.isValid = isValid;
        this.dateCreated = dateCreated;
    }
}
