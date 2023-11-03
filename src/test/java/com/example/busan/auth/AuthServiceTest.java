package com.example.busan.auth;

import com.example.busan.auth.domain.Auth;
import com.example.busan.auth.domain.Member;
import com.example.busan.auth.domain.PasswordEncoder;
import com.example.busan.auth.domain.Region;
import com.example.busan.auth.domain.repository.MemberRepository;
import com.example.busan.auth.dto.LoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("로그인하기")
    void login() {
        //given
        final Member member = new Member("ididididid", "password1234", Region.EMPTY, "부경대", passwordEncoder);
        memberRepository.save(member);

        final LoginRequest request = new LoginRequest("ididididid", "password1234");

        //when
        final Auth login = authService.login(request);

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
}
