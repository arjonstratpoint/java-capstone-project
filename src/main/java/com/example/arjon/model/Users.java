package com.example.arjon.model;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

public record Users(
        @Id
        Integer id,
        String userName,
        String password,
        String email,
        LocalDateTime dateCreated
) {
        public Users(String userName, String password, String email) {
                this(null, userName, password, email, LocalDateTime.now());
        }
}
