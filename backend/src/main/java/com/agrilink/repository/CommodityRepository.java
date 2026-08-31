package com.agrilink.repository;

import com.agrilink.entity.Commodity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommodityRepository extends JpaRepository<Commodity, Long> {

    /**
     * Find a commodity by exact or case-insensitive name.
     * Use case: Searching for commodities like "Tomato" or "Wheat" during price discovery.
     */
    Optional<Commodity> findByNameIgnoreCase(String name);

    /**
     * Check if a commodity exists by name.
     * Use case: Validation before inserting or onboarding commodities.
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Find all commodities belonging to a specific agricultural category.
     * Use case: Category-based filtering (e.g. "Vegetables", "Grains", "Pulses").
     */
    List<Commodity> findByCategoryIgnoreCase(String category);

    /**
     * List all commodities ordered alphabetically by name.
     * Use case: Dropdowns and selection catalogs in the UI.
     */
    List<Commodity> findAllByOrderByNameAsc();
}
