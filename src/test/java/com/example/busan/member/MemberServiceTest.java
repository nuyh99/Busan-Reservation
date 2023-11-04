package com.example.busan.member;

import com.example.busan.DatabaseCleaner;
import com.example.busan.auth.domain.PasswordEncoder;
import com.example.busan.auth.dto.RegisterRequest;
import com.example.busan.auth.service.PhoneAuthenticator;
import com.example.busan.member.domain.Member;
import com.example.busan.member.domain.MemberRepository;
import com.example.busan.member.domain.Region;
import com.example.busan.member.dto.EmailDuplicateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @MockBean
    private PhoneAuthenticator phoneAuthenticator;

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
        given(phoneAuthenticator.getPhone("test@naver.com"))
                .willReturn("01012345678");
        final RegisterRequest request = new RegisterRequest("test@naver.com", "@password1234", "name", Region.GANGNEUNG, "company");

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

}
