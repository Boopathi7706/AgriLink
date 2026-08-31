package com.agrilink.controller;

import com.agrilink.dto.request.RecordMarketPriceRequest;
import com.agrilink.dto.response.MarketPriceResponse;
import com.agrilink.dto.response.PriceDiscoveryResponse;
import com.agrilink.service.MarketPriceService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/market-prices")
public class MarketPriceController {

    private final MarketPriceService marketPriceService;

    public MarketPriceController(MarketPriceService marketPriceService) {
        this.marketPriceService = marketPriceService;
    }

    @PostMapping
    public ResponseEntity<MarketPriceResponse> recordMarketPrice(@Valid @RequestBody RecordMarketPriceRequest request) {
        MarketPriceResponse response = marketPriceService.recordMarketPrice(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/discovery")
    public ResponseEntity<PriceDiscoveryResponse> discoverPrice(
            @RequestParam("commodityId") Long commodityId,
            @RequestParam("priceDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate priceDate) {
        return ResponseEntity.ok(marketPriceService.discoverPrice(commodityId, priceDate));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarketPriceResponse> getMarketPriceById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(marketPriceService.getMarketPriceById(id));
    }

    @GetMapping
    public ResponseEntity<List<MarketPriceResponse>> getMarketPrices(
            @RequestParam(value = "commodityId", required = false) Long commodityId,
            @RequestParam(value = "mandiId", required = false) Long mandiId,
            @RequestParam(value = "priceDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate priceDate,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (priceDate != null && (startDate != null || endDate != null)) {
            throw new IllegalArgumentException("Cannot combine priceDate with startDate or endDate parameters");
        }
        if ((startDate != null && endDate == null) || (startDate == null && endDate != null)) {
            throw new IllegalArgumentException("Both startDate and endDate must be provided for date range queries");
        }
        if (startDate != null && commodityId == null) {
            throw new IllegalArgumentException("Date range queries require commodityId parameter");
        }

        // Case 6: Historical prices for a specific commodity in a specific mandi
        if (commodityId != null && mandiId != null && startDate != null) {
            return ResponseEntity.ok(marketPriceService.getHistoricalPricesByMandiAndCommodity(mandiId, commodityId, startDate, endDate));
        }

        // Case 5: Historical prices for a commodity
        if (commodityId != null && startDate != null && mandiId == null) {
            return ResponseEntity.ok(marketPriceService.getHistoricalPricesByCommodity(commodityId, startDate, endDate));
        }

        // Case 3: Prices for a commodity on a specific date
        if (commodityId != null && priceDate != null && mandiId == null) {
            return ResponseEntity.ok(marketPriceService.getPricesByCommodityAndDate(commodityId, priceDate));
        }

        // Case 4: Prices for a mandi on a specific date
        if (mandiId != null && priceDate != null && commodityId == null) {
            return ResponseEntity.ok(marketPriceService.getPricesByMandiAndDate(mandiId, priceDate));
        }

        // Case 1: Prices for a commodity
        if (commodityId != null && mandiId == null && priceDate == null && startDate == null) {
            return ResponseEntity.ok(marketPriceService.getPricesByCommodity(commodityId));
        }

        // Case 2: Prices for a mandi
        if (mandiId != null && commodityId == null && priceDate == null && startDate == null) {
            return ResponseEntity.ok(marketPriceService.getPricesByMandi(mandiId));
        }

        throw new IllegalArgumentException(
                "Invalid query parameter combination. Supported queries: commodityId, mandiId, " +
                "(commodityId/mandiId + priceDate), or (commodityId [+ mandiId] + startDate + endDate)"
        );
    }
}
