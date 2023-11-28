package com.example.busan.member;

import com.example.busan.DatabaseCleaner;
import com.example.busan.auth.domain.PasswordEncoder;
import com.example.busan.auth.dto.RegisterRequest;
import com.example.busan.member.domain.Member;
import com.example.busan.member.domain.MemberRepository;
import com.example.busan.member.domain.Region;
import com.example.busan.member.dto.EmailDuplicateResponse;
import com.example.busan.member.dto.UpdatePasswordRequest;
import com.example.busan.member.dto.UpdateProfileRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void initDatabase() {
        databaseCleaner.truncate();
    }

    @Test
    @DisplayName("회원가입 하기")
    void register() {
        //given
        final RegisterRequest request = new RegisterRequest("test@naver.com", "@password1234", "name", "0101234", Region.GANGNEUNG, "company");

        //when
        memberService.register(request);

        //then
        assertThat(memberRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("회원탈퇴 하기")
    void withdraw() {
        //given
        final Member member = createMember();

        //when
        memberService.deleteById(member.getEmail());

        //then
        assertThat(memberRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("이메일 중복 확인하기")
    void isDuplicated() {
        //when
        final EmailDuplicateResponse duplicated = memberService.isDuplicated("test@naver.com");

        //then
        assertThat(duplicated.isDuplicated()).isFalse();
    }

    private Member createMember() {
        final Member member = new Member("test@naver.com", "name", "@password1234", Region.GANGNEUNG, "01012345678", "부경대", passwordEncoder);
        return memberRepository.save(member);
    }

    @Test
    @DisplayName("회원 정보 수정하기 - 이름, 회사명, 이메일")
    void updateProfile() {
        //given
        final Member member = createMember();
        final UpdateProfileRequest request = new UpdateProfileRequest("updated", "updated", "test@apple.com");

        //when
        memberService.updateProfile(member.getEmail(), request);

        //then
        final Member updated = memberRepository.findById("test@apple.com").get();
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(updated.getEmail()).isEqualTo("test@apple.com");
            softAssertions.assertThat(updated.getCompany()).isEqualTo("updated");
            softAssertions.assertThat(updated.getName()).isEqualTo("updated");
            softAssertions.assertThat(memberRepository.findById(member.getEmail())).isEmpty();
        });
    }

    @Test
    @DisplayName("분실한 비밀번호 변경하기")
    void updatePassword() {
        //given
        final Member member = createMember();
        final UpdatePasswordRequest request = new UpdatePasswordRequest(member.getEmail(), member.getPhone(), "@@newPassword12");

        //when
        memberService.updatePassword(request);

        final Member updated = memberRepository.findById(member.getEmail()).get();

        //then
        assertDoesNotThrow(() -> passwordEncoder.validateEquals("@@newPassword12", updated.getPassword()));
    }

    @Test
    @DisplayName("분실한 비밀번호 변경하기 - 기존의 이메일과 휴대폰 번호가 일치하지 않으면 예외가 발생한다")
    void updatePassword_fail1() {
        //given
        final Member member = createMember();
        final UpdatePasswordRequest request = new UpdatePasswordRequest("invalid@gmail.com", member.getPhone(), "@@newPassword12");

        //when, then
        assertThatThrownBy(() -> memberService.updatePassword(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("분실한 비밀번호 변경하기 - 기존의 이메일과 휴대폰 번호가 일치하지 않으면 예외가 발생한다")
    void updatePassword_fail2() {
        //given
        final Member member = createMember();
        final UpdatePasswordRequest request = new UpdatePasswordRequest(member.getEmail(), "0101231", "@@newPassword12");

        //when, then
        assertThatThrownBy(() -> memberService.updatePassword(request))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
