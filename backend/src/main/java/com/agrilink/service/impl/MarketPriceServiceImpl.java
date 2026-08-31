package com.agrilink.service.impl;

import com.agrilink.dto.request.RecordMarketPriceRequest;
import com.agrilink.dto.response.MarketPriceResponse;
import com.agrilink.dto.response.PriceDiscoveryResponse;
import com.agrilink.entity.Commodity;
import com.agrilink.entity.Mandi;
import com.agrilink.entity.MarketPrice;
import com.agrilink.exception.DuplicateResourceException;
import com.agrilink.exception.ResourceNotFoundException;
import com.agrilink.repository.CommodityRepository;
import com.agrilink.repository.MandiRepository;
import com.agrilink.repository.MarketPriceRepository;
import com.agrilink.service.MarketPriceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MarketPriceServiceImpl implements MarketPriceService {

    private final MarketPriceRepository marketPriceRepository;
    private final MandiRepository mandiRepository;
    private final CommodityRepository commodityRepository;

    public MarketPriceServiceImpl(MarketPriceRepository marketPriceRepository,
                                  MandiRepository mandiRepository,
                                  CommodityRepository commodityRepository) {
        this.marketPriceRepository = marketPriceRepository;
        this.mandiRepository = mandiRepository;
        this.commodityRepository = commodityRepository;
    }

    @Override
    @Transactional
    public MarketPriceResponse recordMarketPrice(RecordMarketPriceRequest request) {
        Mandi mandi = mandiRepository.findById(request.getMandiId())
                .orElseThrow(() -> new ResourceNotFoundException("Mandi not found with id: " + request.getMandiId()));

        Commodity commodity = commodityRepository.findById(request.getCommodityId())
                .orElseThrow(() -> new ResourceNotFoundException("Commodity not found with id: " + request.getCommodityId()));

        if (marketPriceRepository.findByMandiIdAndCommodityIdAndPriceDate(
                request.getMandiId(), request.getCommodityId(), request.getPriceDate()).isPresent()) {
            throw new DuplicateResourceException(
                    "Market price record already exists for mandi '" + mandi.getName() +
                    "', commodity '" + commodity.getName() + "' on date " + request.getPriceDate()
            );
        }

        MarketPrice marketPrice = new MarketPrice(
                mandi,
                commodity,
                request.getMinPrice(),
                request.getMaxPrice(),
                request.getModalPrice(),
                request.getPriceDate()
        );

        MarketPrice savedMarketPrice = marketPriceRepository.save(marketPrice);
        return toMarketPriceResponse(savedMarketPrice);
    }

    @Override
    public MarketPriceResponse getMarketPriceById(Long id) {
        MarketPrice marketPrice = marketPriceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Market price not found with id: " + id));
        return toMarketPriceResponse(marketPrice);
    }

    @Override
    public List<MarketPriceResponse> getPricesByCommodity(Long commodityId) {
        if (!commodityRepository.existsById(commodityId)) {
            throw new ResourceNotFoundException("Commodity not found with id: " + commodityId);
        }
        return marketPriceRepository.findByCommodityIdOrderByPriceDateDesc(commodityId)
                .stream()
                .map(this::toMarketPriceResponse)
                .toList();
    }

    @Override
    public List<MarketPriceResponse> getPricesByMandi(Long mandiId) {
        if (!mandiRepository.existsById(mandiId)) {
            throw new ResourceNotFoundException("Mandi not found with id: " + mandiId);
        }
        return marketPriceRepository.findByMandiIdOrderByPriceDateDesc(mandiId)
                .stream()
                .map(this::toMarketPriceResponse)
                .toList();
    }

    @Override
    public List<MarketPriceResponse> getPricesByCommodityAndDate(Long commodityId, LocalDate priceDate) {
        if (!commodityRepository.existsById(commodityId)) {
            throw new ResourceNotFoundException("Commodity not found with id: " + commodityId);
        }
        return marketPriceRepository.findByCommodityIdAndPriceDate(commodityId, priceDate)
                .stream()
                .map(this::toMarketPriceResponse)
                .toList();
    }

    @Override
    public List<MarketPriceResponse> getPricesByMandiAndDate(Long mandiId, LocalDate priceDate) {
        if (!mandiRepository.existsById(mandiId)) {
            throw new ResourceNotFoundException("Mandi not found with id: " + mandiId);
        }
        return marketPriceRepository.findByMandiIdAndPriceDate(mandiId, priceDate)
                .stream()
                .map(this::toMarketPriceResponse)
                .toList();
    }

    @Override
    public List<MarketPriceResponse> getHistoricalPricesByCommodity(Long commodityId, LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date (" + startDate + ") must not be after end date (" + endDate + ")");
        }
        if (!commodityRepository.existsById(commodityId)) {
            throw new ResourceNotFoundException("Commodity not found with id: " + commodityId);
        }
        return marketPriceRepository.findByCommodityIdAndPriceDateBetweenOrderByPriceDateDesc(commodityId, startDate, endDate)
                .stream()
                .map(this::toMarketPriceResponse)
                .toList();
    }

    @Override
    public List<MarketPriceResponse> getHistoricalPricesByMandiAndCommodity(Long mandiId, Long commodityId, LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date (" + startDate + ") must not be after end date (" + endDate + ")");
        }
        if (!mandiRepository.existsById(mandiId)) {
            throw new ResourceNotFoundException("Mandi not found with id: " + mandiId);
        }
        if (!commodityRepository.existsById(commodityId)) {
            throw new ResourceNotFoundException("Commodity not found with id: " + commodityId);
        }
        return marketPriceRepository.findByMandiIdAndCommodityIdAndPriceDateBetweenOrderByPriceDateDesc(mandiId, commodityId, startDate, endDate)
                .stream()
                .map(this::toMarketPriceResponse)
                .toList();
    }

    @Override
    public PriceDiscoveryResponse discoverPrice(Long commodityId, LocalDate priceDate) {
        Commodity commodity = commodityRepository.findById(commodityId)
                .orElseThrow(() -> new ResourceNotFoundException("Commodity not found with id: " + commodityId));

        List<MarketPrice> prices = marketPriceRepository.findByCommodityIdAndPriceDate(commodityId, priceDate);
        List<MarketPriceResponse> mandiPriceResponses = prices.stream()
                .map(this::toMarketPriceResponse)
                .toList();

        if (prices.isEmpty()) {
            return new PriceDiscoveryResponse(
                    commodity.getId(),
                    commodity.getName(),
                    commodity.getCategory(),
                    priceDate,
                    null,
                    null,
                    null,
                    0,
                    mandiPriceResponses
            );
        }

        BigDecimal lowestModalPrice = prices.stream()
                .map(MarketPrice::getModalPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal highestModalPrice = prices.stream()
                .map(MarketPrice::getModalPrice)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal sumModalPrice = prices.stream()
                .map(MarketPrice::getModalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal averageModalPrice = sumModalPrice.divide(
                BigDecimal.valueOf(prices.size()),
                2,
                RoundingMode.HALF_UP
        );

        return new PriceDiscoveryResponse(
                commodity.getId(),
                commodity.getName(),
                commodity.getCategory(),
                priceDate,
                lowestModalPrice,
                highestModalPrice,
                averageModalPrice,
                prices.size(),
                mandiPriceResponses
        );
    }

    private MarketPriceResponse toMarketPriceResponse(MarketPrice price) {
        Mandi mandi = price.getMandi();
        Commodity commodity = price.getCommodity();

        return new MarketPriceResponse(
                price.getId(),
                mandi != null ? mandi.getId() : null,
                mandi != null ? mandi.getName() : null,
                mandi != null ? mandi.getDistrict() : null,
                mandi != null ? mandi.getState() : null,
                commodity != null ? commodity.getId() : null,
                commodity != null ? commodity.getName() : null,
                commodity != null ? commodity.getCategory() : null,
                price.getMinPrice(),
                price.getMaxPrice(),
                price.getModalPrice(),
                price.getPriceDate(),
                price.getCreatedAt(),
                price.getUpdatedAt()
        );
    }
}
