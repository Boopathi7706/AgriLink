package com.agrilink.service;

import com.agrilink.dto.request.RecordMarketPriceRequest;
import com.agrilink.dto.response.MarketPriceResponse;
import com.agrilink.dto.response.PriceDiscoveryResponse;

import java.time.LocalDate;
import java.util.List;

public interface MarketPriceService {

    MarketPriceResponse recordMarketPrice(RecordMarketPriceRequest request);

    MarketPriceResponse getMarketPriceById(Long id);

    List<MarketPriceResponse> getPricesByCommodity(Long commodityId);

    List<MarketPriceResponse> getPricesByMandi(Long mandiId);

    List<MarketPriceResponse> getPricesByCommodityAndDate(Long commodityId, LocalDate priceDate);

    List<MarketPriceResponse> getPricesByMandiAndDate(Long mandiId, LocalDate priceDate);

    List<MarketPriceResponse> getHistoricalPricesByCommodity(Long commodityId, LocalDate startDate, LocalDate endDate);

    List<MarketPriceResponse> getHistoricalPricesByMandiAndCommodity(Long mandiId, Long commodityId, LocalDate startDate, LocalDate endDate);

    PriceDiscoveryResponse discoverPrice(Long commodityId, LocalDate priceDate);
}
