package com.example.busan.auth;

import com.example.busan.DatabaseCleaner;
import com.example.busan.auth.domain.Authorized;
import com.example.busan.auth.domain.AutoLogin;
import com.example.busan.auth.domain.AutoLoginManager;
import com.example.busan.auth.domain.AutoLoginRepository;
import com.example.busan.auth.dto.Authentication;
import com.example.busan.auth.exception.UnauthorizedException;
import com.example.busan.member.domain.Role;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
class AuthorizationArgumentResolverTest {

    @Autowired
    private AuthorizationArgumentResolver resolver;
    @Autowired
    private AutoLoginRepository autoLoginRepository;
    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        databaseCleaner.truncate();
    }

    @Test
    @DisplayName("Authorized 어노테이션이 있고, Authentication 타입이면 리졸빙한다")
    void supportsParameter() {
        //given
        final MethodParameter methodParameter = mock(MethodParameter.class);
        given(methodParameter.hasParameterAnnotation(Authorized.class))
                .willReturn(true);
        doReturn(Authentication.class)
                .when(methodParameter).getParameterType();

        //when
        final boolean supports = resolver.supportsParameter(methodParameter);

        //then
        assertThat(supports).isTrue();
    }

    @Test
    @DisplayName("리졸빙하면 Authentication으로 나온다")
    void resolve() {
        //given
        final MethodParameter methodParameter = mock(MethodParameter.class);
        given(methodParameter.hasParameterAnnotation(Authorized.class))
                .willReturn(true);
        doReturn(Authentication.class)
                .when(methodParameter).getParameterType();

        final NativeWebRequest request = mock(NativeWebRequest.class);
        given(request.getAttribute(AuthController.AUTHORIZATION, RequestAttributes.SCOPE_SESSION))
                .willReturn(new Authentication("id", Role.USER));

        //when
        final Object resolved = resolver.resolveArgument(methodParameter, null, request, null);

        //then
        assertThat(resolved).isInstanceOf(Authentication.class);
    }

    @Test
    @DisplayName("세션이 없는 상태였다면 예외가 발생한다")
    void resolve_fail() {
        //given
        final MethodParameter methodParameter = mock(MethodParameter.class);
        final NativeWebRequest request = mock(NativeWebRequest.class);
        final HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        given(methodParameter.hasParameterAnnotation(Authorized.class))
                .willReturn(true);
        doReturn(Authentication.class)
                .when(methodParameter).getParameterType();
        given(request.getNativeRequest(HttpServletRequest.class))
                .willReturn(httpServletRequest);
        given(httpServletRequest.getCookies())
                .willReturn(new Cookie[]{});

        //when, then
        assertThatThrownBy(() -> resolver.resolveArgument(methodParameter, null, request, null))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("자동 로그인 설정이 돼있다면 세션 없이 리졸빙 된다")
    void resolve_auto() {
        //given
        final MethodParameter methodParameter = mock(MethodParameter.class);
        final NativeWebRequest request = mock(NativeWebRequest.class);
        final HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        given(methodParameter.hasParameterAnnotation(Authorized.class))
                .willReturn(true);
        doReturn(Authentication.class)
                .when(methodParameter).getParameterType();
        given(request.getNativeRequest(HttpServletRequest.class))
                .willReturn(httpServletRequest);
        given(httpServletRequest.getCookies())
                .willReturn(new Cookie[]{new Cookie(AutoLoginManager.AUTO_LOGIN_COOKIE_NAME, "fajselkfjalksej")});

        autoLoginRepository.save(new AutoLogin("fajselkfjalksej", new Authentication("email@naver.com", Role.USER)));

        //when
        final Object resolved = resolver.resolveArgument(methodParameter, null, request, null);

        //then
        assertThat(resolved).isInstanceOf(Authentication.class);
    }
}
