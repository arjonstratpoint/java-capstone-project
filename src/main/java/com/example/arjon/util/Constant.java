package com.example.arjon.util;

public class Constant {
    public static final Integer OTP_LENGTH = 4;
    public static final String GENERIC_AUTH_ERROR_MESSAGE = "Invalid username or password";
    public static final String FORGOT_PASSWORD_SUCCESS_MESSAGE = "Password Changed for : %s";
    public static final String FORGOT_PASSWORD_ERROR_MESSAGE = "Invalid Code";
    public static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
    public static final String INVALID_PASSWORD_ERROR_MESSAGE = "invalid, minimum of eight characters, at least one letter and one number";
}
