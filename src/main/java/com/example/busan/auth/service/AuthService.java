package com.example.busan.auth.service;

import com.example.busan.auth.domain.PasswordEncoder;
import com.example.busan.auth.dto.Authentication;
import com.example.busan.auth.dto.FindEmailResponse;
import com.example.busan.auth.dto.LoginRequest;
import com.example.busan.member.domain.Member;
import com.example.busan.member.domain.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(final MemberRepository memberRepository,
                       final PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Authentication login(final LoginRequest request) {
        final Member member = memberRepository.findById(request.email())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 회원 정보입니다."));
        member.checkPassword(request.password(), passwordEncoder);

        return Authentication.from(member);
    }

    public FindEmailResponse findEmailByPhone(final String phone) {
        final List<Member> members = memberRepository.findByPhone(phone);
        return FindEmailResponse.from(members);
    }
}
