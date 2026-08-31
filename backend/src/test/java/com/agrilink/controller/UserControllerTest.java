package com.agrilink.controller;

import com.agrilink.config.SecurityConfig;
import com.agrilink.dto.request.UpdateBuyerProfileRequest;
import com.agrilink.dto.request.UpdateFarmerProfileRequest;
import com.agrilink.dto.response.BuyerProfileResponse;
import com.agrilink.dto.response.FarmerProfileResponse;
import com.agrilink.dto.response.UserResponse;
import com.agrilink.entity.enums.AccountStatus;
import com.agrilink.entity.enums.BuyerType;
import com.agrilink.entity.enums.Role;
import com.agrilink.exception.GlobalExceptionHandler;
import com.agrilink.security.CustomUserDetailsService;
import com.agrilink.security.JwtAccessDeniedHandler;
import com.agrilink.security.JwtAuthenticationEntryPoint;
import com.agrilink.security.JwtAuthenticationFilter;
import com.agrilink.security.JwtService;
import com.agrilink.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class, JwtAccessDeniedHandler.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(username = "ramesh@example.com", roles = {"FARMER"})
    @DisplayName("GET /api/v1/users/me - Authenticated user retrieves own profile")
    void getCurrentUser_Authenticated_Success() throws Exception {
        FarmerProfileResponse fp = new FarmerProfileResponse(
                10L, new BigDecimal("5.0"), "Village A", "Dindigul", "Tamil Nadu", "624001", "Tomato", false, OffsetDateTime.now(), OffsetDateTime.now()
        );
        UserResponse response = new UserResponse(
                1L, "Ramesh", "ramesh@example.com", "9876543210", Role.FARMER, AccountStatus.ACTIVE, fp, null, OffsetDateTime.now(), OffsetDateTime.now()
        );

        when(userService.getCurrentUser()).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("ramesh@example.com"))
                .andExpect(jsonPath("$.farmerProfile.district").value("Dindigul"));
    }

    @Test
    @DisplayName("GET /api/v1/users/me - Unauthenticated request returns 401")
    void getCurrentUser_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "ramesh@example.com", roles = {"FARMER"})
    @DisplayName("PUT /api/v1/users/me/farmer-profile - FARMER role successfully updates farmer profile")
    void updateFarmerProfile_FarmerRole_Success() throws Exception {
        UpdateFarmerProfileRequest request = new UpdateFarmerProfileRequest(
                new BigDecimal("8.0"), "Village B", "Madurai", "Tamil Nadu", "625001", "Chilli"
        );
        FarmerProfileResponse response = new FarmerProfileResponse(
                10L, new BigDecimal("8.0"), "Village B", "Madurai", "Tamil Nadu", "625001", "Chilli", false, OffsetDateTime.now(), OffsetDateTime.now()
        );

        when(userService.updateFarmerProfile(any(UpdateFarmerProfileRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/users/me/farmer-profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.district").value("Madurai"))
                .andExpect(jsonPath("$.farmSizeAcres").value(8.0));
    }

    @Test
    @WithMockUser(username = "suresh@example.com", roles = {"BUYER"})
    @DisplayName("PUT /api/v1/users/me/farmer-profile - BUYER role forbidden from updating farmer profile")
    void updateFarmerProfile_BuyerRole_Forbidden() throws Exception {
        UpdateFarmerProfileRequest request = new UpdateFarmerProfileRequest(
                new BigDecimal("8.0"), "Village B", "Madurai", "Tamil Nadu", "625001", "Chilli"
        );

        mockMvc.perform(put("/api/v1/users/me/farmer-profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "suresh@example.com", roles = {"BUYER"})
    @DisplayName("PUT /api/v1/users/me/buyer-profile - BUYER role successfully updates buyer profile")
    void updateBuyerProfile_BuyerRole_Success() throws Exception {
        UpdateBuyerProfileRequest request = new UpdateBuyerProfileRequest(
                "Super Fresh Agro", BuyerType.WHOLESALER, "GSTIN123", "New Address", "Chennai", "Tamil Nadu", "600001"
        );
        BuyerProfileResponse response = new BuyerProfileResponse(
                20L, "Super Fresh Agro", BuyerType.WHOLESALER, "GSTIN123", "New Address", "Chennai", "Tamil Nadu", "600001", false, OffsetDateTime.now(), OffsetDateTime.now()
        );

        when(userService.updateBuyerProfile(any(UpdateBuyerProfileRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/users/me/buyer-profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.businessName").value("Super Fresh Agro"));
    }

    @Test
    @WithMockUser(username = "ramesh@example.com", roles = {"FARMER"})
    @DisplayName("PUT /api/v1/users/me/buyer-profile - FARMER role forbidden from updating buyer profile")
    void updateBuyerProfile_FarmerRole_Forbidden() throws Exception {
        UpdateBuyerProfileRequest request = new UpdateBuyerProfileRequest(
                "Super Fresh Agro", BuyerType.WHOLESALER, "GSTIN123", "New Address", "Chennai", "Tamil Nadu", "600001"
        );

        mockMvc.perform(put("/api/v1/users/me/buyer-profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
