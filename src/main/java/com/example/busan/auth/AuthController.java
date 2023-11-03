package com.example.busan.auth;

import com.example.busan.auth.domain.Auth;
import com.example.busan.auth.dto.LoginRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(final AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody final LoginRequest request, final HttpSession session) {
        final Auth auth = authService.login(request);
        session.setAttribute(auth.id(), auth);
        return ResponseEntity.noContent().build();
    }
}
