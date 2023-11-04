package com.example.busan.auth;

import com.example.busan.auth.domain.Authorized;
import com.example.busan.auth.dto.Authentication;
import com.example.busan.auth.exception.UnauthorizedException;
import com.example.busan.member.domain.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.Mockito.doReturn;

class AuthorizationArgumentResolverTest {

    private final AuthorizationArgumentResolver resolver = new AuthorizationArgumentResolver();

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
    void resolve() throws Exception {
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
    void resolve_fail() throws Exception {
        //given
        final MethodParameter methodParameter = mock(MethodParameter.class);
        given(methodParameter.hasParameterAnnotation(Authorized.class))
                .willReturn(true);
        doReturn(Authentication.class)
                .when(methodParameter).getParameterType();

        final NativeWebRequest request = mock(NativeWebRequest.class);

        //when, then
        assertThatThrownBy(() -> resolver.resolveArgument(methodParameter, null, request, null))
                .isInstanceOf(UnauthorizedException.class);
    }
}
