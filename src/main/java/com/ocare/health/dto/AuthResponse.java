package com.ocare.health.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private Long userId;
    private String email;
    private String name;
    private String nickname;
    private String message;
}
