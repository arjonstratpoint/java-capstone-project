package com.example.arjon.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordSecurity extends BCryptPasswordEncoder {
}
