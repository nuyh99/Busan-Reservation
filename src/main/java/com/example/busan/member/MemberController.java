package com.example.busan.member;

import com.example.busan.auth.domain.Authorized;
import com.example.busan.auth.dto.Authentication;
import com.example.busan.auth.dto.RegisterRequest;
import com.example.busan.auth.service.PhoneAuthenticator;
import com.example.busan.member.dto.EmailDuplicateResponse;
import com.example.busan.member.dto.UpdatePhoneRequest;
import com.example.busan.member.dto.UpdateProfileRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.busan.auth.AuthController.AUTHORIZATION;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final PhoneAuthenticator phoneAuthenticator;

    public MemberController(final MemberService memberService, final PhoneAuthenticator phoneAuthenticator) {
        this.memberService = memberService;
        this.phoneAuthenticator = phoneAuthenticator;
    }

    @PostMapping
    public ResponseEntity<Void> register(@RequestBody final RegisterRequest request) {
        final String phone = phoneAuthenticator.getPhone(request.email());
        memberService.register(request, phone);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> withdraw(@Authorized final Authentication authentication, final HttpSession session) {
        session.removeAttribute(AUTHORIZATION);
        memberService.deleteById(authentication.email());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{email}")
    public ResponseEntity<EmailDuplicateResponse> isDuplicated(@PathVariable("email") final String email) {
        final EmailDuplicateResponse response = memberService.isDuplicated(email);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<Void> updateProfile(@RequestBody final UpdateProfileRequest request,
                                              @Authorized final Authentication authentication) {
        memberService.updateProfile(authentication.email(), request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/phone")
    public ResponseEntity<Void> updatePhone(@RequestBody final UpdatePhoneRequest request,
                                            @Authorized final Authentication authentication) {
        phoneAuthenticator.authenticate(request.phone());
        memberService.updatePhone(authentication.email(), request.phone());
        return ResponseEntity.noContent().build();
    }
}
