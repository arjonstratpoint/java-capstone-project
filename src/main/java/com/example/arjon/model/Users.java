package com.example.arjon.model;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

/**
 * The Users entity
 *
 */
public record Users(
        @Id
        Integer id,
        String username,
        String password,
        Roles role,
        LocalDateTime dateCreated
) {
        // Used for users registration
        public Users(String username, String password) {
                this(null, username, password, Roles.USER, LocalDateTime.now());
        }
}
