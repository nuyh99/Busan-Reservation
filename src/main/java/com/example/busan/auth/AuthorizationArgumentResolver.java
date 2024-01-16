package com.example.busan.auth;

import com.example.busan.auth.domain.Authorized;
import com.example.busan.auth.domain.AutoLoginManager;
import com.example.busan.auth.dto.Authentication;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static com.example.busan.auth.AuthController.AUTHORIZATION;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_SESSION;

@Component
public class AuthorizationArgumentResolver implements HandlerMethodArgumentResolver {

    private final AutoLoginManager autoLoginManager;

    public AuthorizationArgumentResolver(AutoLoginManager autoLoginManager) {
        this.autoLoginManager = autoLoginManager;
    }

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        final boolean hasAnnotation = parameter.hasParameterAnnotation(Authorized.class);
        final boolean isValidType = parameter.getParameterType() == Authentication.class;

        return hasAnnotation && isValidType;
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter,
                                  final ModelAndViewContainer mavContainer,
                                  final NativeWebRequest webRequest,
                                  final WebDataBinderFactory binderFactory) {
        Object authentication = webRequest.getAttribute(AUTHORIZATION, SCOPE_SESSION);
        if (authentication != null) {
            return authentication;
        }

        final HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        final Authentication autoLoggedIn = autoLoginManager.getAuthentication(request);

        request.getSession().setAttribute(AUTHORIZATION, autoLoggedIn);
        return autoLoggedIn;
    }
}
