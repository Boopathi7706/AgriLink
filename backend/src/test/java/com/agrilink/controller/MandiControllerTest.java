package com.agrilink.controller;

import com.agrilink.config.SecurityConfig;
import com.agrilink.dto.request.CreateMandiRequest;
import com.agrilink.dto.response.MandiResponse;
import com.agrilink.exception.GlobalExceptionHandler;
import com.agrilink.exception.ResourceNotFoundException;
import com.agrilink.service.MandiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MandiController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class MandiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MandiService mandiService;

    @Test
    @DisplayName("POST /api/v1/mandis - Should create mandi and return 201")
    void createMandi_Success() throws Exception {
        CreateMandiRequest request = new CreateMandiRequest(
                "Koyambedu Market", "Chennai", "Tamil Nadu",
                new BigDecimal("13.069400"), new BigDecimal("80.194800")
        );
        MandiResponse response = new MandiResponse(
                1L, "Koyambedu Market", "Chennai", "Tamil Nadu",
                new BigDecimal("13.069400"), new BigDecimal("80.194800"),
                OffsetDateTime.now(), OffsetDateTime.now()
        );

        when(mandiService.createMandi(any(CreateMandiRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/mandis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Koyambedu Market"))
                .andExpect(jsonPath("$.district").value("Chennai"))
                .andExpect(jsonPath("$.state").value("Tamil Nadu"));
    }

    @Test
    @DisplayName("POST /api/v1/mandis - Should return 400 when coordinates or name are invalid")
    void createMandi_ValidationFailure() throws Exception {
        CreateMandiRequest request = new CreateMandiRequest(
                "", "", "",
                new BigDecimal("150.0"), new BigDecimal("200.0")
        );

        mockMvc.perform(post("/api/v1/mandis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.name").exists())
                .andExpect(jsonPath("$.validationErrors.district").exists())
                .andExpect(jsonPath("$.validationErrors.state").exists())
                .andExpect(jsonPath("$.validationErrors.latitude").exists())
                .andExpect(jsonPath("$.validationErrors.longitude").exists());
    }

    @Test
    @DisplayName("GET /api/v1/mandis - Should return all mandis")
    void getAllMandis_Success() throws Exception {
        MandiResponse response = new MandiResponse(
                1L, "Koyambedu Market", "Chennai", "Tamil Nadu",
                new BigDecimal("13.069400"), new BigDecimal("80.194800"),
                OffsetDateTime.now(), OffsetDateTime.now()
        );
        when(mandiService.getAllMandis()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/mandis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Koyambedu Market"));
    }

    @Test
    @DisplayName("GET /api/v1/mandis?state=Tamil Nadu - Should return mandis in state")
    void getMandisByState_Success() throws Exception {
        MandiResponse response = new MandiResponse(
                1L, "Koyambedu Market", "Chennai", "Tamil Nadu",
                new BigDecimal("13.069400"), new BigDecimal("80.194800"),
                OffsetDateTime.now(), OffsetDateTime.now()
        );
        when(mandiService.getMandisByState("Tamil Nadu")).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/mandis").param("state", "Tamil Nadu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].state").value("Tamil Nadu"));
    }

    @Test
    @DisplayName("GET /api/v1/mandis?state=Tamil Nadu&district=Chennai - Should return mandis in state and district")
    void getMandisByStateAndDistrict_Success() throws Exception {
        MandiResponse response = new MandiResponse(
                1L, "Koyambedu Market", "Chennai", "Tamil Nadu",
                new BigDecimal("13.069400"), new BigDecimal("80.194800"),
                OffsetDateTime.now(), OffsetDateTime.now()
        );
        when(mandiService.getMandisByStateAndDistrict("Tamil Nadu", "Chennai")).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/mandis")
                        .param("state", "Tamil Nadu")
                        .param("district", "Chennai"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].district").value("Chennai"));
    }

    @Test
    @DisplayName("GET /api/v1/mandis?district=Chennai - Should return 400 when district provided without state")
    void getMandisByDistrictOnly_ThrowsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/mandis").param("district", "Chennai"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("District filter requires state parameter to be provided"));
    }

    @Test
    @DisplayName("GET /api/v1/mandis/{id} - Should return mandi by ID")
    void getMandiById_Success() throws Exception {
        MandiResponse response = new MandiResponse(
                1L, "Koyambedu Market", "Chennai", "Tamil Nadu",
                new BigDecimal("13.069400"), new BigDecimal("80.194800"),
                OffsetDateTime.now(), OffsetDateTime.now()
        );
        when(mandiService.getMandiById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/mandis/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Koyambedu Market"));
    }

    @Test
    @DisplayName("GET /api/v1/mandis/{id} - Should return 400 when path variable is not a valid number")
    void getMandiById_InvalidPathVariableType() throws Exception {
        mockMvc.perform(get("/api/v1/mandis/invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Invalid value 'invalid' for parameter 'id'"))
                .andExpect(jsonPath("$.path").value("/api/v1/mandis/invalid"));
    }

    @Test
    @DisplayName("GET /api/v1/mandis/{id} - Should return 404 when mandi not found")
    void getMandiById_NotFound() throws Exception {
        when(mandiService.getMandiById(eq(99L)))
                .thenThrow(new ResourceNotFoundException("Mandi not found with id: 99"));

        mockMvc.perform(get("/api/v1/mandis/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Mandi not found with id: 99"));
    }
}
