package com.example.arjon.util;

import org.springframework.security.core.Authentication;

public interface AuthenticationFacade {
    Integer getUserIdFromAuthentication();
}
