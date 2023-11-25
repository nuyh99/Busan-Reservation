package com.example.busan.reservation;

import com.example.busan.auth.AuthController;
import com.example.busan.auth.dto.Authentication;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.Objects.isNull;

@Component
public class LoginUserAuditorAware implements AuditorAware<String> {

    private final HttpSession httpSession;

    public LoginUserAuditorAware(final HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    @Override
    public Optional<String> getCurrentAuditor() {
        final Authentication authentication = (Authentication) httpSession.getAttribute(AuthController.AUTHORIZATION);
        if (isNull(authentication)) {
            return Optional.empty();
        }

        return Optional.of(authentication.email());
    }
}
