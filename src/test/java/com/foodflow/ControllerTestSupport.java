package com.foodflow;

import com.foodflow.identity.infrastructure.UserAuthentication;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

public final class ControllerTestSupport {

    public static final Long AUTHENTICATED_USER_ID = 77L;

    private static final UserAuthentication AUTHENTICATED_USER = new UserAuthentication(
            AUTHENTICATED_USER_ID,
            "owner@foodflow.test",
            List.of(new SimpleGrantedAuthority("ROLE_USER"))
    );

    private ControllerTestSupport() {
    }

    public static HandlerMethodArgumentResolver authenticatedUserArgumentResolver() {
        return new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.hasParameterAnnotation(AuthenticationPrincipal.class)
                        && UserAuthentication.class.isAssignableFrom(parameter.getParameterType());
            }

            @Override
            public Object resolveArgument(MethodParameter parameter,
                                          ModelAndViewContainer mavContainer,
                                          NativeWebRequest webRequest,
                                          WebDataBinderFactory binderFactory) {
                return AUTHENTICATED_USER;
            }
        };
    }
}
