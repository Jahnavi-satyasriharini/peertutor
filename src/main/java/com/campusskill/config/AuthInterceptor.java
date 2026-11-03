package com.campusskill.config;

import com.campusskill.service.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.campusskill.service.AuthService.SESSION_USER_ID;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SESSION_USER_ID) == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Please log in to continue.");
        }
        return true;
    }
}
