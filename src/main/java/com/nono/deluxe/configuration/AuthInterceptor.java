package com.nono.deluxe.configuration;

import com.nono.deluxe.application.client.TokenClient;
import com.nono.deluxe.configuration.annotation.Auth;
import com.nono.deluxe.domain.user.Role;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final TokenClient tokenClient;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        Auth auth = handlerMethod.getMethodAnnotation(Auth.class);
        if (auth == null) {
            return true;
        }

        String token = request.getHeader("Authorization");
        System.out.println("token" + token);

        if (auth.role().equals(Role.ROLE_ADMIN)) {
            return tokenClient.isActiveAdmin(token);
        }

        if (auth.role().equals(Role.ROLE_MANAGER)) {
            return tokenClient.isActiveManager(token);
        }

        if (auth.role().equals(Role.ROLE_PARTICIPANT)) {
            return tokenClient.isActiveParticipant(token);
        }

        return false;
    }
}
