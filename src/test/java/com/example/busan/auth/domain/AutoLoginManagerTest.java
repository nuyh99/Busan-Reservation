package com.example.busan.auth.domain;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class AutoLoginManagerTest {

    @Autowired
    private AutoLoginManager autoLoginManager;

    @Test
    @DisplayName("자동 로그인 쿠키가 있으면 true를 리턴한다")
    void isAuto() {
        //given
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        given(request.getCookies())
                .willReturn(new Cookie[]{
                        new Cookie(AutoLoginManager.AUTO_LOGIN_COOKIE_NAME, "value")});

        //when
        final boolean isAuto = autoLoginManager.isAuto(request);

        //then
        assertThat(isAuto).isTrue();
    }

    @Test
    @DisplayName("자동 로그인 쿠키가 없으면 false를 리턴한다")
    void isAuto_false() {
        //given
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        given(request.getCookies())
                .willReturn(new Cookie[]{});

        //when
        final boolean isAuto = autoLoginManager.isAuto(request);

        //then
        assertThat(isAuto).isFalse();
    }
}
