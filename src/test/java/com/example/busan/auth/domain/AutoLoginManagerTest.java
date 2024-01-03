package com.example.busan.auth.domain;

import com.example.busan.DatabaseCleaner;
import com.example.busan.auth.dto.Authentication;
import com.example.busan.member.domain.Role;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class AutoLoginManagerTest {

    @Autowired
    private AutoLoginManager autoLoginManager;
    @Autowired
    private AutoLoginRepository autoLoginRepository;
    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        databaseCleaner.truncate();
    }

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

    @Test
    @DisplayName("자동 로그인용 쿠키를 설정할 수 있다")
    void setAutoCookie() {
        //given
        final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        final HttpSession session = Mockito.mock(HttpSession.class);
        final Authentication authentication = new Authentication("email@gmail.com", Role.USER);

        given(session.getId())
                .willReturn("fjseilafjliejlasijfeli21321");

        //when
        autoLoginManager.setAutoCookie(session, response, authentication);

        //then
        verify(response, times(1)).addCookie(any());
        assertThat(autoLoginRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("자동 로그인 쿠키에서 인증 정보를 가져올 수 있다")
    void getAuthentication() {
        //given
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        final HttpSession session = Mockito.mock(HttpSession.class);
        final Authentication authentication = new Authentication("email@gmail.com", Role.USER);

        final String id = "fjseilafjliejlasijfeli21321";
        given(session.getId())
                .willReturn(id);
        given(request.getCookies())
                .willReturn(new Cookie[]{new Cookie(AutoLoginManager.AUTO_LOGIN_COOKIE_NAME, id)});
        autoLoginManager.setAutoCookie(session, response, authentication);

        //when
        final Authentication autoLoggedIn = autoLoginManager.getAuthentication(request);

        //then
        assertThat(autoLoggedIn.email()).isEqualTo("email@gmail.com");
    }

    @Test
    @DisplayName("자동 로그인 쿠키를 제거할 수 있다")
    void removeAutoLogin() {
        //given
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        final HttpSession session = Mockito.mock(HttpSession.class);
        final Authentication authentication = new Authentication("email@gmail.com", Role.USER);

        final String id = "fjseilafjliejlasijfeli21321";
        given(request.getCookies())
                .willReturn(new Cookie[]{new Cookie(AutoLoginManager.AUTO_LOGIN_COOKIE_NAME, id)});
        given(session.getId())
                .willReturn(id);
        autoLoginManager.setAutoCookie(session, response, authentication);

        //when
        autoLoginManager.removeAutoLogin(request, response);

        //then
        assertThat(autoLoginRepository.findAll()).isEmpty();
    }
}
