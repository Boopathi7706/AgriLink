package com.agrilink.repository;

import com.agrilink.entity.MarketPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MarketPriceRepository extends JpaRepository<MarketPrice, Long> {

    /**
     * Find historical price records for a commodity across all mandis, ordered by newest date first.
     * Use case: Viewing the price history timeline for a crop.
     */
    List<MarketPrice> findByCommodityIdOrderByPriceDateDesc(Long commodityId);

    /**
     * Find market prices for a commodity across all mandis on a specific date.
     * Use case: Daily price discovery across India for a specific crop (e.g. today's Tomato prices).
     */
    List<MarketPrice> findByCommodityIdAndPriceDate(Long commodityId, LocalDate priceDate);

    /**
     * Find market prices for a commodity on a given date ordered by modal price ascending.
     * Use case: Buyer price comparison to identify mandis with the most competitive / lowest prices.
     */
    List<MarketPrice> findByCommodityIdAndPriceDateOrderByModalPriceAsc(Long commodityId, LocalDate priceDate);

    /**
     * Find market prices for a commodity on a given date ordered by modal price descending.
     * Use case: Farmer price discovery to identify mandis offering highest price realization.
     */
    List<MarketPrice> findByCommodityIdAndPriceDateOrderByModalPriceDesc(Long commodityId, LocalDate priceDate);

    /**
     * Find all commodity prices recorded in a single mandi on a specific date.
     * Use case: Daily mandi bulletin or market summary dashboard.
     */
    List<MarketPrice> findByMandiIdAndPriceDate(Long mandiId, LocalDate priceDate);

    /**
     * Find all historical price records recorded at a single mandi, newest first.
     * Use case: Individual mandi historical report.
     */
    List<MarketPrice> findByMandiIdOrderByPriceDateDesc(Long mandiId);

    /**
     * Find the unique daily price record for a specific commodity at a specific mandi on a given date.
     * Use case: Point lookup, deduplication, and daily data ingestion.
     */
    Optional<MarketPrice> findByMandiIdAndCommodityIdAndPriceDate(Long mandiId, Long commodityId, LocalDate priceDate);

    /**
     * Find commodity price records within a date range across all mandis, ordered by date descending.
     * Use case: Price trend graphs, historical analytics, and ML model training window.
     */
    List<MarketPrice> findByCommodityIdAndPriceDateBetweenOrderByPriceDateDesc(Long commodityId, LocalDate startDate, LocalDate endDate);

    /**
     * Find commodity price records within a date range for a specific mandi, ordered by date descending.
     * Use case: Localized mandi price trend charts.
     */
    List<MarketPrice> findByMandiIdAndCommodityIdAndPriceDateBetweenOrderByPriceDateDesc(Long mandiId, Long commodityId, LocalDate startDate, LocalDate endDate);
}
