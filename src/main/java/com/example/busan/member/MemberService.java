package com.example.busan.member;

import com.example.busan.auth.domain.PasswordEncoder;
import com.example.busan.auth.dto.RegisterRequest;
import com.example.busan.member.domain.Member;
import com.example.busan.member.domain.MemberRepository;
import com.example.busan.member.dto.EmailDuplicateResponse;
import com.example.busan.member.dto.UpdatePasswordRequest;
import com.example.busan.member.dto.UpdateProfileRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(final MemberRepository memberRepository, final PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(final RegisterRequest request) {
        final Member member = new Member(
                request.email(),
                request.name(),
                request.password(),
                request.region(),
                request.company(),
                request.phone(),
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
        final Member member = findOrThrow(email);

        final Member updated = member.updateProfile(request.email(), request.company(), request.name());
        memberRepository.save(updated);
        memberRepository.delete(member);
    }

    private Member findOrThrow(final String email) {
        return memberRepository.findById(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    @Transactional
    public void updatePhone(final String email, final String phone) {
        final Member member = findOrThrow(email);
        member.updatePhone(phone);
    }

    @Transactional
    public void updatePassword(final UpdatePasswordRequest request) {
        final Member member = memberRepository.findByEmailAndPhone(request.email(), request.phone())
                .orElseThrow(() -> new IllegalArgumentException("올바르지 않은 계정 정보입니다."));

        member.updatePassword(request.password());
    }
}
