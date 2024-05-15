package com.example.arjon.model;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

public record Users(
        @Id
        Integer id,
        String username,
        String password,
        LocalDateTime dateCreated
) {
        public Users(String username, String password) {
                this(null, username, password, LocalDateTime.now());
        }
}
