package com.agrilink.security;

import com.agrilink.entity.User;
import com.agrilink.entity.enums.AccountStatus;
import com.agrilink.entity.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "expirationMinutes", 1440L);
    }

    private User createSampleUser() {
        User user = new User("Ramesh Kumar", "ramesh@example.com", "hash", "9876543210", Role.FARMER, AccountStatus.ACTIVE);
        user.setId(10L);
        return user;
    }

    @Test
    @DisplayName("Generate token and validate extracted claims")
    void generateToken_AndExtractClaims() {
        User user = createSampleUser();
        String token = jwtService.generateToken(user);

        assertThat(token).isNotBlank();
        assertThat(jwtService.isTokenValid(token)).isTrue();
        assertThat(jwtService.extractUserId(token)).isEqualTo(10L);
        assertThat(jwtService.extractEmail(token)).isEqualTo("ramesh@example.com");
        assertThat(jwtService.extractRole(token)).isEqualTo("FARMER");
    }

    @Test
    @DisplayName("Validate token with UserPrincipal")
    void isTokenValid_WithUserPrincipal() {
        User user = createSampleUser();
        UserPrincipal principal = UserPrincipal.create(user);
        String token = jwtService.generateToken(user);

        assertThat(jwtService.isTokenValid(token, principal)).isTrue();
    }

    @Test
    @DisplayName("Token validation fails for malformed token")
    void isTokenValid_MalformedToken() {
        assertThat(jwtService.isTokenValid("invalid.jwt.token")).isFalse();
        assertThat(jwtService.extractUserId("invalid.jwt.token")).isNull();
    }
}
