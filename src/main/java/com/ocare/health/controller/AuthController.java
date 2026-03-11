package com.ocare.health.controller;

import com.ocare.health.dto.AuthResponse;
import com.ocare.health.dto.LoginRequest;
import com.ocare.health.dto.SignupRequest;
import com.ocare.health.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(
            @Valid @RequestBody SignupRequest request,
            HttpSession session) {
        AuthResponse response = authService.signup(request, session);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpSession session) {
        AuthResponse response = authService.login(request, session);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        authService.logout(session);
        return ResponseEntity.ok("로그아웃되었습니다");
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String email = (String) session.getAttribute("email");

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(AuthResponse.builder()
                .userId(userId)
                .email(email)
                .message("인증된 사용자입니다")
                .build());
    }
}
