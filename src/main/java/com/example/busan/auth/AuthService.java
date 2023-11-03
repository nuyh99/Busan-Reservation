package com.example.busan.auth;

import com.example.busan.auth.domain.Auth;
import com.example.busan.auth.domain.Member;
import com.example.busan.auth.domain.PasswordEncoder;
import com.example.busan.auth.domain.repository.MemberRepository;
import com.example.busan.auth.dto.LoginRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(final MemberRepository memberRepository, final PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Auth login(final LoginRequest request) {
        final Member member = memberRepository.findById(request.id())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 회원 정보입니다."));
        passwordEncoder.validateEquals(request.password(), member.getPassword());

        return Auth.from(member);
    }
}
