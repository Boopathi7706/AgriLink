package com.agrilink.controller;

import com.agrilink.dto.request.CreateCommodityRequest;
import com.agrilink.dto.response.CommodityResponse;
import com.agrilink.service.CommodityService;
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
@RequestMapping("/api/v1/commodities")
public class CommodityController {

    private final CommodityService commodityService;

    public CommodityController(CommodityService commodityService) {
        this.commodityService = commodityService;
    }

    @PostMapping
    public ResponseEntity<CommodityResponse> createCommodity(@Valid @RequestBody CreateCommodityRequest request) {
        CommodityResponse response = commodityService.createCommodity(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CommodityResponse>> getAllCommodities(
            @RequestParam(value = "category", required = false) String category) {
        if (category != null && !category.isBlank()) {
            return ResponseEntity.ok(commodityService.getCommoditiesByCategory(category));
        }
        return ResponseEntity.ok(commodityService.getAllCommodities());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommodityResponse> getCommodityById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(commodityService.getCommodityById(id));
    }
}
