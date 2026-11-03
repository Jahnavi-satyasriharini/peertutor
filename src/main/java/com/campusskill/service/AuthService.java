package com.campusskill.service;

import com.campusskill.dto.LoginRequest;
import com.campusskill.dto.SignupRequest;
import com.campusskill.dto.UserResponse;
import com.campusskill.model.Role;
import com.campusskill.model.User;
import com.campusskill.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {

    public static final String SESSION_USER_ID = "userId";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String collegeEmailDomain;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       @Value("${app.college-email-domain}") String collegeEmailDomain) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.collegeEmailDomain = collegeEmailDomain.toLowerCase(Locale.ROOT);
    }

    @Transactional
    public UserResponse signup(SignupRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (!email.endsWith("@" + collegeEmailDomain)) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Please sign up with your college email ending in @" + collegeEmailDomain + ".");
        }
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "An account with this email already exists.");
        }
        User user = new User(
                request.name().trim(),
                email,
                passwordEncoder.encode(request.password()),
                request.branch().trim(),
                request.year(),
                TextUtils.blankToNull(request.bio()),
                Role.STUDENT
        );
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email().trim())
                .orElseThrow(AuthService::invalidCredentials);
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw invalidCredentials();
        }
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserResponse currentUser(HttpSession session) {
        Long userId = currentUserId(session);
        return userRepository.findById(userId)
                .map(UserResponse::from)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Please log in to continue."));
    }

    @Transactional(readOnly = true)
    public Long currentUserId(HttpSession session) {
        Object userId = session == null ? null : session.getAttribute(SESSION_USER_ID);
        if (userId == null || !userRepository.existsById((Long) userId)) {
            if (session != null) {
                session.invalidate();
            }
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Please log in to continue.");
        }
        return (Long) userId;
    }

    public void startSession(HttpServletRequest request, Long userId) {
        HttpSession previous = request.getSession(false);
        if (previous != null) {
            previous.invalidate();
        }
        request.getSession(true).setAttribute(SESSION_USER_ID, userId);
    }

    public void endSession(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }

    private static ApiException invalidCredentials() {
        return new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
    }
}
