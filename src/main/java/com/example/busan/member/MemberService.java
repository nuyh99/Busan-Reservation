package com.example.busan.member;

import com.example.busan.auth.domain.PasswordEncoder;
import com.example.busan.auth.dto.RegisterRequest;
import com.example.busan.auth.service.PhoneAuthenticator;
import com.example.busan.member.domain.Member;
import com.example.busan.member.domain.MemberRepository;
import com.example.busan.member.dto.EmailDuplicateResponse;
import com.example.busan.member.dto.UpdateProfileRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final PhoneAuthenticator phoneAuthenticator;

    public MemberService(final MemberRepository memberRepository, final PasswordEncoder passwordEncoder, final PhoneAuthenticator phoneAuthenticator) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.phoneAuthenticator = phoneAuthenticator;
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

    public EmailDuplicateResponse isDuplicated(final String email) {
        final boolean duplicated = memberRepository.existsById(email);
        return new EmailDuplicateResponse(duplicated);
    }

    @Transactional
    public void updateProfile(final String email, final UpdateProfileRequest request) {
        final Member member = memberRepository.findById(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        final Member updated = member.updateProfile(request.email(), request.company(), request.name());
        memberRepository.save(updated);
        memberRepository.delete(member);
    }
}
