package com.example.arjon.model;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

public record Users(
        @Id
        Integer id,
        String userName,
        String password,
        LocalDateTime dateCreated
) {
        public Users(String userName, String password) {
                this(null, userName, password, LocalDateTime.now());
        }
}
