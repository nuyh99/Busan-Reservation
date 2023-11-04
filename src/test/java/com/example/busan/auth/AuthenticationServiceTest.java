package com.example.busan.auth;

import com.example.busan.DatabaseCleaner;
import com.example.busan.auth.domain.PasswordEncoder;
import com.example.busan.auth.dto.Authentication;
import com.example.busan.auth.dto.LoginRequest;
import com.example.busan.auth.dto.RegisterRequest;
import com.example.busan.auth.service.AuthService;
import com.example.busan.auth.service.PhoneAuthenticator;
import com.example.busan.member.domain.Member;
import com.example.busan.member.domain.MemberRepository;
import com.example.busan.member.domain.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class AuthenticationServiceTest {

    @Autowired
    private AuthService authService;
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
    @DisplayName("로그인하기")
    void login() {
        //given
        final Member member = new Member("ididididid", "password1234", Region.GANGNEUNG, "01012345678", "부경대", passwordEncoder);
        memberRepository.save(member);

        final LoginRequest request = new LoginRequest("ididididid", "password1234");

        //when
        final Authentication login = authService.login(request);

        //then
        assertThat(login.id()).isEqualTo("ididididid");
    }

    @Test
    @DisplayName("로그인 실패")
    void login_fail() {
        //given
        final LoginRequest request = new LoginRequest("ididididid", "password1234");

        //when, then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("회원가입 하기")
    void register() {
        //given
        given(phoneAuthenticator.getPhone("idididi"))
                .willReturn("01012345678");
        final RegisterRequest request = new RegisterRequest("idididi", "password1234", Region.GANGNEUNG, "company");

        //when
        authService.register(request);

        //then
        assertThat(memberRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("회원탈퇴 하기")
    void withdraw() {
        //given
        final Member member = createMember();

        //when
        authService.deleteById(member.getId());

        //then
        assertThat(memberRepository.findAll()).isEmpty();
    }

    private Member createMember() {
        final Member member = new Member("ididididid", "password1234", Region.GANGNEUNG, "01012345678", "부경대", passwordEncoder);
        return memberRepository.save(member);
    }
}
