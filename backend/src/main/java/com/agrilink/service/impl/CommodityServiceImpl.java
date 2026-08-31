package com.agrilink.service.impl;

import com.agrilink.dto.request.CreateCommodityRequest;
import com.agrilink.dto.response.CommodityResponse;
import com.agrilink.entity.Commodity;
import com.agrilink.exception.DuplicateResourceException;
import com.agrilink.exception.ResourceNotFoundException;
import com.agrilink.repository.CommodityRepository;
import com.agrilink.service.CommodityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CommodityServiceImpl implements CommodityService {

    private final CommodityRepository commodityRepository;

    public CommodityServiceImpl(CommodityRepository commodityRepository) {
        this.commodityRepository = commodityRepository;
    }

    @Override
    @Transactional
    public CommodityResponse createCommodity(CreateCommodityRequest request) {
        String trimmedName = request.getName().trim();
        String trimmedCategory = request.getCategory().trim();

        if (commodityRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new DuplicateResourceException("Commodity with name '" + trimmedName + "' already exists");
        }

        Commodity commodity = new Commodity(trimmedName, trimmedCategory);
        Commodity savedCommodity = commodityRepository.save(commodity);
        return toCommodityResponse(savedCommodity);
    }

    @Override
    public CommodityResponse getCommodityById(Long id) {
        Commodity commodity = commodityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commodity not found with id: " + id));
        return toCommodityResponse(commodity);
    }

    @Override
    public List<CommodityResponse> getAllCommodities() {
        return commodityRepository.findAllByOrderByNameAsc()
                .stream()
                .map(this::toCommodityResponse)
                .toList();
    }

    @Override
    public List<CommodityResponse> getCommoditiesByCategory(String category) {
        return commodityRepository.findByCategoryIgnoreCase(category.trim())
                .stream()
                .map(this::toCommodityResponse)
                .toList();
    }

    private CommodityResponse toCommodityResponse(Commodity commodity) {
        return new CommodityResponse(
                commodity.getId(),
                commodity.getName(),
                commodity.getCategory(),
                commodity.getCreatedAt(),
                commodity.getUpdatedAt()
        );
    }
}
