package com.agrilink.config;

import com.agrilink.controller.CommodityController;
import com.agrilink.controller.MandiController;
import com.agrilink.controller.MarketPriceController;
import com.agrilink.dto.response.MarketPriceResponse;
import com.agrilink.exception.GlobalExceptionHandler;
import com.agrilink.security.CustomUserDetailsService;
import com.agrilink.security.JwtAccessDeniedHandler;
import com.agrilink.security.JwtAuthenticationEntryPoint;
import com.agrilink.security.JwtAuthenticationFilter;
import com.agrilink.security.JwtService;
import com.agrilink.service.CommodityService;
import com.agrilink.service.MandiService;
import com.agrilink.service.MarketPriceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({MandiController.class, CommodityController.class, MarketPriceController.class})
@Import({SecurityConfig.class, GlobalExceptionHandler.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class, JwtAccessDeniedHandler.class})
class CorsConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MandiService mandiService;

    @MockBean
    private CommodityService commodityService;

    @MockBean
    private MarketPriceService marketPriceService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @DisplayName("OPTIONS /api/v1/mandis - Preflight request from allowed origin should succeed with CORS headers")
    void preflight_Mandis_Success() throws Exception {
        mockMvc.perform(options("/api/v1/mandis")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5173")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "Authorization,Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5173"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"));
    }

    @Test
    @DisplayName("OPTIONS /api/v1/commodities - Preflight request from allowed origin should succeed with CORS headers")
    void preflight_Commodities_Success() throws Exception {
        mockMvc.perform(options("/api/v1/commodities")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5173")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5173"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"));
    }

    @Test
    @DisplayName("OPTIONS /api/v1/market-prices - Preflight request from allowed origin should succeed with CORS headers")
    void preflight_MarketPrices_Success() throws Exception {
        mockMvc.perform(options("/api/v1/market-prices")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5173")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "Authorization,Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5173"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"));
    }

    @Test
    @DisplayName("GET /api/v1/mandis - Request from allowed origin should contain Access-Control-Allow-Origin header")
    void get_Mandis_ContainsCorsHeader() throws Exception {
        when(mandiService.getAllMandis()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/mandis")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5173"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5173"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"));
    }

    @Test
    @DisplayName("GET /api/v1/commodities - Request from allowed origin should contain Access-Control-Allow-Origin header")
    void get_Commodities_ContainsCorsHeader() throws Exception {
        when(commodityService.getAllCommodities()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/commodities")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5173"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5173"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"));
    }

    @Test
    @DisplayName("GET /api/v1/market-prices/1 - Request from allowed origin should contain Access-Control-Allow-Origin header")
    void get_MarketPriceById_ContainsCorsHeader() throws Exception {
        MarketPriceResponse response = new MarketPriceResponse(
                1L, 1L, "Koyambedu Market", "Chennai", "Tamil Nadu",
                2L, "Tomato", "Vegetables",
                new java.math.BigDecimal("25.00"), new java.math.BigDecimal("35.00"), new java.math.BigDecimal("30.00"),
                java.time.LocalDate.of(2026, 9, 1), java.time.OffsetDateTime.now(), java.time.OffsetDateTime.now()
        );
        when(marketPriceService.getMarketPriceById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/market-prices/1")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5173"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5173"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"));
    }
}
