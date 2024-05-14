package com.example.arjon.model;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

public record User(
        @Id
        Integer id,
        @NotBlank
        String userName,
        @NotBlank
        String password,
        @NotBlank
        String email,
        LocalDateTime dateCreated
) {
}
