package com.ocare.health.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class PasswordEncryptor {

    private static final String ALGORITHM = "AES";
    private final SecretKeySpec secretKey;

    public PasswordEncryptor(@Value("${password.secret:health}") String secret) {
        // 'health' 키를 16바이트로 확장 (AES-128)
        String paddedSecret = String.format("%-16s", secret).replace(' ', '0');
        this.secretKey = new SecretKeySpec(paddedSecret.getBytes(StandardCharsets.UTF_8), ALGORITHM);
    }

    public String encrypt(String plainPassword) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(plainPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("비밀번호 암호화 실패", e);
        }
    }

    public String decrypt(String encryptedPassword) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("비밀번호 복호화 실패", e);
        }
    }

    public boolean matches(String plainPassword, String encryptedPassword) {
        try {
            String decrypted = decrypt(encryptedPassword);
            return plainPassword.equals(decrypted);
        } catch (Exception e) {
            return false;
        }
    }
}
