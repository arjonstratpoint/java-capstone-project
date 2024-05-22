package com.example.arjon.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static com.example.arjon.util.Constant.INVALID_PASSWORD_ERROR_MESSAGE;
import static com.example.arjon.util.Constant.PASSWORD_REGEX;

/**
 * A request object for changing password
 */
public record ChangePasswordRequest(
        @NotBlank
        String currentPassword,
        @NotBlank
        @Pattern(regexp = PASSWORD_REGEX, message = INVALID_PASSWORD_ERROR_MESSAGE)
        String newPassword
) {
}
