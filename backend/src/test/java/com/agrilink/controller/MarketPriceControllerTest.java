package com.agrilink.controller;

import com.agrilink.config.SecurityConfig;
import com.agrilink.dto.request.RecordMarketPriceRequest;
import com.agrilink.dto.response.MarketPriceResponse;
import com.agrilink.dto.response.PriceDiscoveryResponse;
import com.agrilink.exception.GlobalExceptionHandler;
import com.agrilink.exception.ResourceNotFoundException;
import com.agrilink.security.CustomUserDetailsService;
import com.agrilink.security.JwtAccessDeniedHandler;
import com.agrilink.security.JwtAuthenticationEntryPoint;
import com.agrilink.security.JwtAuthenticationFilter;
import com.agrilink.security.JwtService;
import com.agrilink.service.MarketPriceService;
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
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MarketPriceController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class, JwtAccessDeniedHandler.class})
class MarketPriceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MarketPriceService marketPriceService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    private MarketPriceResponse samplePriceResponse() {
        return new MarketPriceResponse(
                1L, 1L, "Koyambedu Market", "Chennai", "Tamil Nadu",
                2L, "Tomato", "Vegetables",
                new BigDecimal("25.00"), new BigDecimal("35.00"), new BigDecimal("30.00"),
                LocalDate.of(2026, 9, 1), OffsetDateTime.now(), OffsetDateTime.now()
        );
    }

    @Test
    @WithMockUser(roles = "FARMER")
    @DisplayName("POST /api/v1/market-prices - Should record price and return 201 when FARMER")
    void recordMarketPrice_Success() throws Exception {
        RecordMarketPriceRequest request = new RecordMarketPriceRequest(
                1L, 2L,
                new BigDecimal("25.00"), new BigDecimal("35.00"), new BigDecimal("30.00"),
                LocalDate.of(2026, 9, 1)
        );

        when(marketPriceService.recordMarketPrice(any(RecordMarketPriceRequest.class)))
                .thenReturn(samplePriceResponse());

        mockMvc.perform(post("/api/v1/market-prices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.mandiName").value("Koyambedu Market"))
                .andExpect(jsonPath("$.commodityName").value("Tomato"))
                .andExpect(jsonPath("$.modalPrice").value(30.00));
    }

    @Test
    @WithMockUser(roles = "FARMER")
    @DisplayName("POST /api/v1/market-prices - Should return 400 when body fails validation")
    void recordMarketPrice_ValidationFailure() throws Exception {
        RecordMarketPriceRequest request = new RecordMarketPriceRequest(
                null, null,
                new BigDecimal("-5.00"), new BigDecimal("10.00"), new BigDecimal("20.00"),
                null
        );

        mockMvc.perform(post("/api/v1/market-prices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.mandiId").exists())
                .andExpect(jsonPath("$.validationErrors.commodityId").exists())
                .andExpect(jsonPath("$.validationErrors.minPrice").exists())
                .andExpect(jsonPath("$.validationErrors.priceDate").exists());
    }

    @Test
    @WithMockUser(roles = "FARMER")
    @DisplayName("POST /api/v1/market-prices - Should return 400 when request body is malformed JSON")
    void recordMarketPrice_MalformedJson() throws Exception {
        mockMvc.perform(post("/api/v1/market-prices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mandiId\": 1, \"minPrice\": }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Malformed or unreadable request body"))
                .andExpect(jsonPath("$.path").value("/api/v1/market-prices"));
    }

    @Test
    @DisplayName("GET /api/v1/market-prices/{id} - Should return market price by ID (Public)")
    void getMarketPriceById_Success() throws Exception {
        when(marketPriceService.getMarketPriceById(1L)).thenReturn(samplePriceResponse());

        mockMvc.perform(get("/api/v1/market-prices/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.commodityName").value("Tomato"));
    }

    @Test
    @DisplayName("GET /api/v1/market-prices/{id} - Should return 400 when path variable is not a valid number")
    void getMarketPriceById_InvalidPathVariableType() throws Exception {
        mockMvc.perform(get("/api/v1/market-prices/xyz"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Invalid value 'xyz' for parameter 'id'"))
                .andExpect(jsonPath("$.path").value("/api/v1/market-prices/xyz"));
    }

    @Test
    @DisplayName("GET /api/v1/market-prices/{id} - Should return 404 when not found")
    void getMarketPriceById_NotFound() throws Exception {
        when(marketPriceService.getMarketPriceById(eq(99L)))
                .thenThrow(new ResourceNotFoundException("Market price not found with id: 99"));

        mockMvc.perform(get("/api/v1/market-prices/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Market price not found with id: 99"));
    }

    @Test
    @DisplayName("GET /api/v1/market-prices/discovery - Should return price discovery aggregate (Public)")
    void discoverPrice_Success() throws Exception {
        PriceDiscoveryResponse discovery = new PriceDiscoveryResponse(
                2L, "Tomato", "Vegetables", LocalDate.of(2026, 9, 1),
                new BigDecimal("25.00"), new BigDecimal("35.00"), new BigDecimal("30.00"),
                1, List.of(samplePriceResponse())
        );

        when(marketPriceService.discoverPrice(eq(2L), eq(LocalDate.of(2026, 9, 1))))
                .thenReturn(discovery);

        mockMvc.perform(get("/api/v1/market-prices/discovery")
                        .param("commodityId", "2")
                        .param("priceDate", "2026-09-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commodityId").value(2L))
                .andExpect(jsonPath("$.commodityName").value("Tomato"))
                .andExpect(jsonPath("$.lowestModalPrice").value(25.00))
                .andExpect(jsonPath("$.highestModalPrice").value(35.00))
                .andExpect(jsonPath("$.averageModalPrice").value(30.00))
                .andExpect(jsonPath("$.totalMandisReporting").value(1))
                .andExpect(jsonPath("$.mandiPrices[0].mandiName").value("Koyambedu Market"));
    }

    @Test
    @DisplayName("GET /api/v1/market-prices/discovery - Should return 400 when priceDate is invalid date format")
    void discoverPrice_InvalidDateFormat() throws Exception {
        mockMvc.perform(get("/api/v1/market-prices/discovery")
                        .param("commodityId", "1")
                        .param("priceDate", "invalid-date"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Invalid value 'invalid-date' for parameter 'priceDate'"))
                .andExpect(jsonPath("$.path").value("/api/v1/market-prices/discovery"));
    }

    @Test
    @DisplayName("GET /api/v1/market-prices?commodityId=2 - Should return prices for commodity (Public)")
    void getPricesByCommodity_Success() throws Exception {
        when(marketPriceService.getPricesByCommodity(2L)).thenReturn(List.of(samplePriceResponse()));

        mockMvc.perform(get("/api/v1/market-prices").param("commodityId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].commodityId").value(2L));
    }

    @Test
    @DisplayName("GET /api/v1/market-prices?commodityId=abc - Should return 400 when commodityId is not a number")
    void getPricesByCommodity_InvalidParameterType() throws Exception {
        mockMvc.perform(get("/api/v1/market-prices").param("commodityId", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Invalid value 'abc' for parameter 'commodityId'"))
                .andExpect(jsonPath("$.path").value("/api/v1/market-prices"));
    }

    @Test
    @DisplayName("GET /api/v1/market-prices?mandiId=1 - Should return prices for mandi (Public)")
    void getPricesByMandi_Success() throws Exception {
        when(marketPriceService.getPricesByMandi(1L)).thenReturn(List.of(samplePriceResponse()));

        mockMvc.perform(get("/api/v1/market-prices").param("mandiId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].mandiId").value(1L));
    }

    @Test
    @DisplayName("GET /api/v1/market-prices?commodityId=2&priceDate=2026-09-01 - Should return commodity prices on date (Public)")
    void getPricesByCommodityAndDate_Success() throws Exception {
        when(marketPriceService.getPricesByCommodityAndDate(2L, LocalDate.of(2026, 9, 1)))
                .thenReturn(List.of(samplePriceResponse()));

        mockMvc.perform(get("/api/v1/market-prices")
                        .param("commodityId", "2")
                        .param("priceDate", "2026-09-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].commodityId").value(2L));
    }

    @Test
    @DisplayName("GET /api/v1/market-prices?mandiId=1&priceDate=2026-09-01 - Should return mandi prices on date (Public)")
    void getPricesByMandiAndDate_Success() throws Exception {
        when(marketPriceService.getPricesByMandiAndDate(1L, LocalDate.of(2026, 9, 1)))
                .thenReturn(List.of(samplePriceResponse()));

        mockMvc.perform(get("/api/v1/market-prices")
                        .param("mandiId", "1")
                        .param("priceDate", "2026-09-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].mandiId").value(1L));
    }

    @Test
    @DisplayName("GET /api/v1/market-prices?commodityId=2&startDate=2026-08-01&endDate=2026-09-01 - Should return historical prices (Public)")
    void getHistoricalPricesByCommodity_Success() throws Exception {
        when(marketPriceService.getHistoricalPricesByCommodity(2L, LocalDate.of(2026, 8, 1), LocalDate.of(2026, 9, 1)))
                .thenReturn(List.of(samplePriceResponse()));

        mockMvc.perform(get("/api/v1/market-prices")
                        .param("commodityId", "2")
                        .param("startDate", "2026-08-01")
                        .param("endDate", "2026-09-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].commodityId").value(2L));
    }

    @Test
    @DisplayName("GET /api/v1/market-prices?commodityId=2&mandiId=1&startDate=2026-08-01&endDate=2026-09-01 - Should return mandi commodity history (Public)")
    void getHistoricalPricesByMandiAndCommodity_Success() throws Exception {
        when(marketPriceService.getHistoricalPricesByMandiAndCommodity(1L, 2L, LocalDate.of(2026, 8, 1), LocalDate.of(2026, 9, 1)))
                .thenReturn(List.of(samplePriceResponse()));

        mockMvc.perform(get("/api/v1/market-prices")
                        .param("commodityId", "2")
                        .param("mandiId", "1")
                        .param("startDate", "2026-08-01")
                        .param("endDate", "2026-09-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].mandiId").value(1L));
    }

    @Test
    @DisplayName("GET /api/v1/market-prices - Should return 400 when no parameters are provided")
    void getMarketPrices_NoParams_ThrowsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/market-prices"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("GET /api/v1/market-prices - Should return 400 when priceDate is combined with startDate/endDate")
    void getMarketPrices_InvalidDateCombo_ThrowsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/market-prices")
                        .param("commodityId", "2")
                        .param("priceDate", "2026-09-01")
                        .param("startDate", "2026-08-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("GET /api/v1/market-prices - Should return 400 when startDate is missing endDate")
    void getMarketPrices_MissingEndDate_ThrowsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/market-prices")
                        .param("commodityId", "2")
                        .param("startDate", "2026-08-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
