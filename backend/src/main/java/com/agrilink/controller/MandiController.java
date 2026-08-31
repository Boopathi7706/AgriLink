package com.agrilink.controller;

import com.agrilink.dto.request.CreateMandiRequest;
import com.agrilink.dto.response.MandiResponse;
import com.agrilink.service.MandiService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mandis")
public class MandiController {

    private final MandiService mandiService;

    public MandiController(MandiService mandiService) {
        this.mandiService = mandiService;
    }

    @PostMapping
    public ResponseEntity<MandiResponse> createMandi(@Valid @RequestBody CreateMandiRequest request) {
        MandiResponse response = mandiService.createMandi(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MandiResponse>> getMandis(
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "district", required = false) String district) {
        boolean hasState = state != null && !state.isBlank();
        boolean hasDistrict = district != null && !district.isBlank();

        if (hasState && hasDistrict) {
            return ResponseEntity.ok(mandiService.getMandisByStateAndDistrict(state, district));
        } else if (hasState) {
            return ResponseEntity.ok(mandiService.getMandisByState(state));
        } else if (hasDistrict) {
            throw new IllegalArgumentException("District filter requires state parameter to be provided");
        } else {
            return ResponseEntity.ok(mandiService.getAllMandis());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<MandiResponse> getMandiById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(mandiService.getMandiById(id));
    }
}
