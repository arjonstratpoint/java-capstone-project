package com.example.arjon.model;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

public record User(
        @Id
        Integer id,
        String userName,
        String password,
        String email,
        LocalDateTime dateCreated
) {
}
