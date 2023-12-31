package com.example.busan.auth;

import com.example.busan.auth.domain.AutoLoginManager;
import com.example.busan.auth.dto.AuthenticatePhoneRequest;
import com.example.busan.auth.dto.Authentication;
import com.example.busan.auth.dto.FindEmailResponse;
import com.example.busan.auth.dto.LoginRequest;
import com.example.busan.auth.service.AuthService;
import com.example.busan.auth.service.PhoneAuthenticator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    public static final String AUTHORIZATION = "Authorization";

    private final AuthService authService;
    private final PhoneAuthenticator phoneAuthenticator;
    private final AutoLoginManager autoLoginManager;

    public AuthController(final AuthService authService,
                          final PhoneAuthenticator phoneAuthenticator,
                          AutoLoginManager autoLoginManager) {
        this.authService = authService;
        this.phoneAuthenticator = phoneAuthenticator;
        this.autoLoginManager = autoLoginManager;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody final LoginRequest request,
                                      final HttpServletResponse httpServletResponse,
                                      final HttpSession session) {
        final Authentication authentication = authService.login(request);

        if (request.isAuto()) {
            autoLoginManager.setAutoCookie(session, httpServletResponse, authentication);
        }

        session.setAttribute(AUTHORIZATION, authentication);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(final HttpSession session,
                                       final HttpServletRequest request,
                                       final HttpServletResponse response) {
        session.removeAttribute(AUTHORIZATION);
        autoLoginManager.removeAutoLogin(request, response);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/phone")
    public ResponseEntity<Void> sendCode(@RequestBody final AuthenticatePhoneRequest request) {
        phoneAuthenticator.sendCode(request.phone());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/phone")
    public ResponseEntity<Void> authenticate(@RequestParam("code") String code) {
        phoneAuthenticator.authenticate(code);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/email")
    public ResponseEntity<FindEmailResponse> findEmailByPhone(@RequestParam("phone") final String phone) {
        phoneAuthenticator.validateAuthenticated(phone);
        final FindEmailResponse response = authService.findEmailByPhone(phone);
        return ResponseEntity.ok(response);
    }
}
