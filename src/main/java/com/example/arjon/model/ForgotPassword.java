package com.example.arjon.model;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

/**
 * The ForgotPassword entity
 *
 */
public record ForgotPassword(
        @Id
        Integer id,
        Integer userId,
        String code,
        Boolean isValid,
        LocalDateTime dateCreated
) {
    // Used for creating a forgot password request
    public ForgotPassword(Integer userId, String code) {
        this(null, userId, code, true, LocalDateTime.now());
    }

    // Used for updating existing forgot password requests
    public ForgotPassword(ForgotPassword fp) {
        this(fp.id(), fp.userId(), fp.code(), false, fp.dateCreated());
    }
}
