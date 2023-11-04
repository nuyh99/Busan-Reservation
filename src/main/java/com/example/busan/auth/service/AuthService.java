package com.example.busan.auth.service;

import com.example.busan.auth.domain.PasswordEncoder;
import com.example.busan.auth.dto.Authentication;
import com.example.busan.auth.dto.LoginRequest;
import com.example.busan.auth.dto.RegisterRequest;
import com.example.busan.member.domain.Member;
import com.example.busan.member.domain.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final PhoneAuthenticator phoneAuthenticator;

    public AuthService(final MemberRepository memberRepository,
                       final PasswordEncoder passwordEncoder,
                       final PhoneAuthenticator phoneAuthenticator) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.phoneAuthenticator = phoneAuthenticator;
    }

    public Authentication login(final LoginRequest request) {
        final Member member = memberRepository.findById(request.email())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 회원 정보입니다."));
        passwordEncoder.validateEquals(request.password(), member.getPassword());

        return Authentication.from(member);
    }

    @Transactional
    public void register(final RegisterRequest request) {
        final Member member = new Member(
                request.email(),
                request.name(),
                request.password(),
                request.region(),
                request.company(),
                phoneAuthenticator.getPhone(request.email()),
                passwordEncoder);

        memberRepository.save(member);
    }

    @Transactional
    public void deleteById(final String memberId) {
        memberRepository.deleteById(memberId);
    }
}
