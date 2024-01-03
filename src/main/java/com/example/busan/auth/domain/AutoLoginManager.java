package com.example.busan.auth.domain;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class AutoLoginManager {

    public static final String AUTO_LOGIN_COOKIE_NAME = "AUTO";

    public boolean isAuto(final HttpServletRequest request) {
        if (request == null) {
            return false;
        }

        return Arrays.stream(request.getCookies())
                .anyMatch(cookie -> cookie.getName().equals("AUTO"));
    }
}
