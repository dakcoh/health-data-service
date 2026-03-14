package com.ocare.health.controller;

import com.ocare.health.dto.ApiResponse;
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
    public ResponseEntity<ApiResponse<AuthResponse>> signup(
            @Valid @RequestBody SignupRequest request,
            HttpSession session) {
        AuthResponse response = authService.signup(request, session);
        return ResponseEntity.ok(ApiResponse.success("회원가입 완료", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpSession session) {
        AuthResponse response = authService.login(request, session);
        return ResponseEntity.ok(ApiResponse.success("로그인 완료", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpSession session) {
        authService.logout(session);
        return ResponseEntity.ok(ApiResponse.success("로그아웃되었습니다"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponse>> getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String email = (String) session.getAttribute("email");

        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("인증되지 않은 사용자입니다"));
        }

        AuthResponse authResponse = AuthResponse.builder()
                .userId(userId)
                .email(email)
                .build();
        return ResponseEntity.ok(ApiResponse.success("인증된 사용자입니다", authResponse));
    }
}
