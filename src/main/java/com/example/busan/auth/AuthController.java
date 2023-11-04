package com.example.busan.auth;

import com.example.busan.auth.domain.Authorized;
import com.example.busan.auth.dto.Authentication;
import com.example.busan.auth.dto.LoginRequest;
import com.example.busan.auth.dto.RegisterRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    public static final String AUTHORIZATION = "Authorization";

    private final AuthService authService;

    public AuthController(final AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody final LoginRequest request, final HttpSession session) {
        final Authentication authentication = authService.login(request);
        session.setAttribute(AUTHORIZATION, authentication);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(final HttpSession session) {
        session.removeAttribute(AUTHORIZATION);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody final RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> withdraw(@Authorized final Authentication authentication, final HttpSession session) {
        session.removeAttribute(AUTHORIZATION);
        authService.deleteById(authentication.id());
        return ResponseEntity.noContent().build();
    }
}
