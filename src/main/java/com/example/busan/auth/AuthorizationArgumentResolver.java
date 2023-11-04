package com.example.busan.auth;

import com.example.busan.auth.domain.Authorized;
import com.example.busan.auth.dto.Authentication;
import com.example.busan.auth.exception.UnauthorizedException;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static com.example.busan.auth.AuthController.AUTHORIZATION;
import static java.util.Objects.isNull;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_SESSION;

@Component
public class AuthorizationArgumentResolver implements HandlerMethodArgumentResolver {
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
                                  final WebDataBinderFactory binderFactory) throws Exception {
        final Object authentication = webRequest.getAttribute(AUTHORIZATION, SCOPE_SESSION);
        if (isNull(authentication)) {
            throw new UnauthorizedException();
        }

        return authentication;
    }
}
