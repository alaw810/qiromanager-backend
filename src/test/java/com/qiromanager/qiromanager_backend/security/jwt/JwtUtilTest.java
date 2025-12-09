package com.qiromanager.qiromanager_backend.security.jwt;

import com.qiromanager.qiromanager_backend.domain.user.Role;
import com.qiromanager.qiromanager_backend.domain.user.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private User user;

    @BeforeEach
    void setup() {
        String secret = "this-is-a-very-secure-secret-key-123456-must-be-long";
        long expirationMillis = 1000 * 60 * 60; // 1 hora
        jwtUtil = new JwtUtil(secret, expirationMillis);

        user = new User();
        user.setUsername("johndoe");
        user.setRole(Role.USER);
    }

    @Test
    void generateToken_shouldContainCorrectUsernameAndRole() {
        String token = jwtUtil.generateToken(user);

        assertThat(token).isNotNull();
        assertThat(jwtUtil.extractUsername(token)).isEqualTo("johndoe");
        assertThat(jwtUtil.extractRole(token)).isEqualTo("USER");
    }

    @Test
    void isTokenValid_shouldReturnTrueForValidToken() {
        String token = jwtUtil.generateToken(user);

        boolean isValid = jwtUtil.isTokenValid(token, user);

        assertThat(isValid).isTrue();
    }

    @Test
    void isTokenValid_shouldReturnFalseForTokenWithDifferentUser() {
        String token = jwtUtil.generateToken(user);

        User otherUser = new User();
        otherUser.setUsername("anotherUser");
        otherUser.setRole(Role.USER);

        boolean isValid = jwtUtil.isTokenValid(token, otherUser);

        assertThat(isValid).isFalse();
    }

    @Test
    void extractUsername_shouldThrowExceptionForTamperedToken() {
        String token = jwtUtil.generateToken(user);

        String tamperedToken = token.substring(0, token.length() - 5) + "ABCDE";

        assertThatThrownBy(() -> jwtUtil.extractUsername(tamperedToken))
                .isInstanceOf(SignatureException.class)
                .hasMessageContaining("JWT signature does not match");
    }

    @Test
    void extractUsername_shouldThrowExceptionForGarbageToken() {
        String garbageToken = "this.is.not.a.token";

        assertThatThrownBy(() -> jwtUtil.extractUsername(garbageToken))
                .isInstanceOf(MalformedJwtException.class);
    }

    @Test
    void tokenShouldExpireProperly() throws InterruptedException {
        JwtUtil shortLivedJwt = new JwtUtil(
                "this-is-a-very-secure-secret-key-123456-must-be-long",
                50
        );

        String token = shortLivedJwt.generateToken(user);

        Thread.sleep(100);

        assertThatThrownBy(() -> shortLivedJwt.extractUsername(token))
                .isInstanceOf(ExpiredJwtException.class);
    }
}