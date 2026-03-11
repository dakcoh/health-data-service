package com.ocare.health.service;

import com.ocare.health.domain.User;
import com.ocare.health.dto.AuthResponse;
import com.ocare.health.dto.LoginRequest;
import com.ocare.health.dto.SignupRequest;
import com.ocare.health.repository.UserRepository;
import com.ocare.health.security.PasswordEncryptor;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncryptor passwordEncryptor;

    @Transactional
    public AuthResponse signup(SignupRequest request, HttpSession session) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다");
        }

        // 비밀번호 암호화
        String encryptedPassword = passwordEncryptor.encrypt(request.getPassword());

        // 사용자 생성
        User user = User.builder()
                .name(request.getName())
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password(encryptedPassword)
                .build();

        User savedUser = userRepository.save(user);

        // 세션에 사용자 정보 저장
        session.setAttribute("userId", savedUser.getId());
        session.setAttribute("email", savedUser.getEmail());

        return AuthResponse.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .name(savedUser.getName())
                .nickname(savedUser.getNickname())
                .message("회원가입이 완료되었습니다")
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request, HttpSession session) {
        // 사용자 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다"));

        // 비밀번호 검증
        if (!passwordEncryptor.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다");
        }

        // 세션에 사용자 정보 저장
        session.setAttribute("userId", user.getId());
        session.setAttribute("email", user.getEmail());

        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .message("로그인되었습니다")
                .build();
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }
}
