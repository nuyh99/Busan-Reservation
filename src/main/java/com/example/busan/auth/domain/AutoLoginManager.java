package com.example.busan.auth.domain;

import com.example.busan.auth.dto.Authentication;
import com.example.busan.auth.exception.UnauthorizedException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class AutoLoginManager {

    public static final String AUTO_LOGIN_COOKIE_NAME = "AUTO";

    private final AutoLoginRepository autoLoginRepository;

    public AutoLoginManager(AutoLoginRepository autoLoginRepository) {
        this.autoLoginRepository = autoLoginRepository;
    }

    public boolean isAuto(final HttpServletRequest request) {
        if (request == null) {
            return false;
        }

        return Arrays.stream(request.getCookies())
                .anyMatch(cookie -> cookie.getName().equals(AUTO_LOGIN_COOKIE_NAME));
    }

    public void setAutoCookie(final HttpSession httpSession,
                              final HttpServletResponse response,
                              final Authentication authentication) {
        final String id = httpSession.getId();

        autoLoginRepository.save(new AutoLogin(id, authentication));

        final Cookie cookie = new Cookie(AUTO_LOGIN_COOKIE_NAME, id);
        cookie.setPath("/");
        cookie.setMaxAge(Integer.MAX_VALUE);
        response.addCookie(cookie);
    }

    public Authentication getAuthentication(final HttpServletRequest request) {
        final Cookie autoLoggedInCookie = getAutoLoggedInCookie(request);
        if (autoLoggedInCookie == null) {
            throw new UnauthorizedException();
        }

        final String id = autoLoggedInCookie.getValue();
        return autoLoginRepository.findById(id)
                .orElseThrow(UnauthorizedException::new)
                .getAuthentication();
    }

    private Cookie getAutoLoggedInCookie(final HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(AUTO_LOGIN_COOKIE_NAME))
                .findAny()
                .orElse(null);
    }

    public void removeAutoLogin(final HttpServletRequest request,
                                final HttpServletResponse response) {
        final Cookie autoLoggedInCookie = getAutoLoggedInCookie(request);

        final Cookie cookie = new Cookie(AUTO_LOGIN_COOKIE_NAME, "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        if (autoLoggedInCookie != null) {
            autoLoginRepository.deleteById(autoLoggedInCookie.getValue());
        }
    }
}
