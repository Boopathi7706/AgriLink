package com.agrilink.repository;

import com.agrilink.entity.Mandi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MandiRepository extends JpaRepository<Mandi, Long> {

    /**
     * Find all mandis in a specific state and district.
     * Use case: Farmer or buyer searching for local nearby mandis in their district.
     */
    List<Mandi> findByStateIgnoreCaseAndDistrictIgnoreCase(String state, String district);

    /**
     * Find all mandis in a specific state.
     * Use case: State-level mandi exploration.
     */
    List<Mandi> findByStateIgnoreCase(String state);

    /**
     * Find a unique mandi by its composite natural key (name, district, state).
     * Use case: Ingestion and lookup of APMC markets without ID knowledge.
     */
    Optional<Mandi> findByNameIgnoreCaseAndDistrictIgnoreCaseAndStateIgnoreCase(String name, String district, String state);

    /**
     * Check if a mandi already exists at a given location.
     * Use case: Preventing duplicate mandi registrations.
     */
    boolean existsByNameIgnoreCaseAndDistrictIgnoreCaseAndStateIgnoreCase(String name, String district, String state);

    /**
     * List all mandis ordered alphabetically by name.
     * Use case: Populating search filters and market directory views.
     */
    List<Mandi> findAllByOrderByNameAsc();
}
