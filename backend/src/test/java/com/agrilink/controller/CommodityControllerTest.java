package com.agrilink.controller;

import com.agrilink.config.SecurityConfig;
import com.agrilink.dto.request.CreateCommodityRequest;
import com.agrilink.dto.response.CommodityResponse;
import com.agrilink.exception.GlobalExceptionHandler;
import com.agrilink.exception.ResourceNotFoundException;
import com.agrilink.security.CustomUserDetailsService;
import com.agrilink.security.JwtAccessDeniedHandler;
import com.agrilink.security.JwtAuthenticationEntryPoint;
import com.agrilink.security.JwtAuthenticationFilter;
import com.agrilink.security.JwtService;
import com.agrilink.service.CommodityService;
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

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommodityController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class, JwtAccessDeniedHandler.class})
class CommodityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommodityService commodityService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/v1/commodities - Should create commodity and return 201 when ADMIN")
    void createCommodity_Success() throws Exception {
        CreateCommodityRequest request = new CreateCommodityRequest("Tomato", "Vegetables");
        CommodityResponse response = new CommodityResponse(1L, "Tomato", "Vegetables", OffsetDateTime.now(), OffsetDateTime.now());

        when(commodityService.createCommodity(any(CreateCommodityRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/commodities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Tomato"))
                .andExpect(jsonPath("$.category").value("Vegetables"));
    }

    @Test
    @DisplayName("POST /api/v1/commodities - Should return 401 when unauthenticated")
    void createCommodity_Unauthenticated_Forbidden() throws Exception {
        CreateCommodityRequest request = new CreateCommodityRequest("Tomato", "Vegetables");

        mockMvc.perform(post("/api/v1/commodities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/v1/commodities - Should return 400 when request body is invalid")
    void createCommodity_ValidationFailure() throws Exception {
        CreateCommodityRequest request = new CreateCommodityRequest("", "");

        mockMvc.perform(post("/api/v1/commodities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.name").exists())
                .andExpect(jsonPath("$.validationErrors.category").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/v1/commodities - Should return 400 when request body is empty JSON object")
    void createCommodity_EmptyBodyValidationFailure() throws Exception {
        mockMvc.perform(post("/api/v1/commodities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.name").exists())
                .andExpect(jsonPath("$.validationErrors.category").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/v1/commodities - Should return 400 when request body is malformed JSON")
    void createCommodity_MalformedJson() throws Exception {
        String malformedJson = "{\"name\": \"Tomato\", \"category\": }";

        mockMvc.perform(post("/api/v1/commodities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Malformed or unreadable request body"))
                .andExpect(jsonPath("$.path").value("/api/v1/commodities"));
    }

    @Test
    @DisplayName("GET /api/v1/commodities - Should return all commodities with 200 (Public)")
    void getAllCommodities_Success() throws Exception {
        CommodityResponse response = new CommodityResponse(1L, "Tomato", "Vegetables", OffsetDateTime.now(), OffsetDateTime.now());
        when(commodityService.getAllCommodities()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/commodities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Tomato"));
    }

    @Test
    @DisplayName("GET /api/v1/commodities?category=Vegetables - Should return filtered commodities (Public)")
    void getCommoditiesByCategory_Success() throws Exception {
        CommodityResponse response = new CommodityResponse(1L, "Tomato", "Vegetables", OffsetDateTime.now(), OffsetDateTime.now());
        when(commodityService.getCommoditiesByCategory("Vegetables")).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/commodities").param("category", "Vegetables"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].category").value("Vegetables"));
    }

    @Test
    @DisplayName("GET /api/v1/commodities/{id} - Should return commodity by ID (Public)")
    void getCommodityById_Success() throws Exception {
        CommodityResponse response = new CommodityResponse(1L, "Tomato", "Vegetables", OffsetDateTime.now(), OffsetDateTime.now());
        when(commodityService.getCommodityById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/commodities/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Tomato"));
    }

    @Test
    @DisplayName("GET /api/v1/commodities/{id} - Should return 400 when path variable is not a valid number")
    void getCommodityById_InvalidPathVariableType() throws Exception {
        mockMvc.perform(get("/api/v1/commodities/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Invalid value 'abc' for parameter 'id'"))
                .andExpect(jsonPath("$.path").value("/api/v1/commodities/abc"));
    }

    @Test
    @DisplayName("GET /api/v1/commodities/{id} - Should return 404 when commodity not found")
    void getCommodityById_NotFound() throws Exception {
        when(commodityService.getCommodityById(eq(99L)))
                .thenThrow(new ResourceNotFoundException("Commodity not found with id: 99"));

        mockMvc.perform(get("/api/v1/commodities/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Commodity not found with id: 99"));
    }
}
