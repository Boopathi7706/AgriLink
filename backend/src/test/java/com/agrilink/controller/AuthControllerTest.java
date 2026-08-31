package com.agrilink.controller;

import com.agrilink.config.SecurityConfig;
import com.agrilink.dto.request.FarmerProfileRequest;
import com.agrilink.dto.request.LoginRequest;
import com.agrilink.dto.request.RegisterRequest;
import com.agrilink.dto.response.AuthResponse;
import com.agrilink.dto.response.FarmerProfileResponse;
import com.agrilink.dto.response.UserResponse;
import com.agrilink.entity.enums.AccountStatus;
import com.agrilink.entity.enums.Role;
import com.agrilink.exception.AccountDisabledException;
import com.agrilink.exception.DuplicateResourceException;
import com.agrilink.exception.GlobalExceptionHandler;
import com.agrilink.security.CustomUserDetailsService;
import com.agrilink.security.JwtAccessDeniedHandler;
import com.agrilink.security.JwtAuthenticationEntryPoint;
import com.agrilink.security.JwtAuthenticationFilter;
import com.agrilink.security.JwtService;
import com.agrilink.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class, JwtAccessDeniedHandler.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    private AuthResponse sampleFarmerAuthResponse() {
        FarmerProfileResponse fp = new FarmerProfileResponse(
                10L, new BigDecimal("5.0"), "Village A", "Dindigul", "Tamil Nadu", "624001", "Tomato", false, OffsetDateTime.now(), OffsetDateTime.now()
        );
        UserResponse user = new UserResponse(
                1L, "Ramesh", "ramesh@example.com", "9876543210", Role.FARMER, AccountStatus.ACTIVE, fp, null, OffsetDateTime.now(), OffsetDateTime.now()
        );
        return new AuthResponse("sample-jwt-token", 86400L, user);
    }

    @Test
    @DisplayName("POST /api/v1/auth/register - Successfully registers farmer and returns 201")
    void register_Farmer_Success() throws Exception {
        FarmerProfileRequest fpReq = new FarmerProfileRequest(
                new BigDecimal("5.0"), "Village A", "Dindigul", "Tamil Nadu", "624001", "Tomato"
        );
        RegisterRequest request = new RegisterRequest(
                "Ramesh", "ramesh@example.com", "Password@123", "9876543210", Role.FARMER, fpReq, null
        );

        when(authService.register(any(RegisterRequest.class))).thenReturn(sampleFarmerAuthResponse());

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("sample-jwt-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.user.id").value(1L))
                .andExpect(jsonPath("$.user.role").value("FARMER"))
                .andExpect(jsonPath("$.user.farmerProfile.district").value("Dindigul"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/register - Fails validation on invalid input and returns 400")
    void register_ValidationFailure() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "", "invalid-email", "short", "123", null, null, null
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.name").exists())
                .andExpect(jsonPath("$.validationErrors.email").exists())
                .andExpect(jsonPath("$.validationErrors.password").exists())
                .andExpect(jsonPath("$.validationErrors.phoneNumber").exists())
                .andExpect(jsonPath("$.validationErrors.role").exists());
    }

    @Test
    @DisplayName("POST /api/v1/auth/register - Rejects duplicate email with 409 Conflict")
    void register_DuplicateEmail_Conflict() throws Exception {
        FarmerProfileRequest fpReq = new FarmerProfileRequest(
                new BigDecimal("5.0"), "Village A", "Dindigul", "Tamil Nadu", "624001", "Tomato"
        );
        RegisterRequest request = new RegisterRequest(
                "Ramesh", "ramesh@example.com", "Password@123", "9876543210", Role.FARMER, fpReq, null
        );

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new DuplicateResourceException("User with email 'ramesh@example.com' already exists"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("User with email 'ramesh@example.com' already exists"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - Successfully logs in and returns 200 OK")
    void login_Success() throws Exception {
        LoginRequest request = new LoginRequest("ramesh@example.com", "Password@123");
        when(authService.login(any(LoginRequest.class))).thenReturn(sampleFarmerAuthResponse());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("sample-jwt-token"))
                .andExpect(jsonPath("$.user.email").value("ramesh@example.com"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - Returns 401 on invalid credentials")
    void login_BadCredentials() throws Exception {
        LoginRequest request = new LoginRequest("ramesh@example.com", "WrongPassword");
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Invalid email or password"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - Returns 403 on suspended account")
    void login_SuspendedAccount() throws Exception {
        LoginRequest request = new LoginRequest("ramesh@example.com", "Password@123");
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new AccountDisabledException("Account is suspended or deactivated. Please contact support."));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("Account is suspended or deactivated. Please contact support."));
    }
}
