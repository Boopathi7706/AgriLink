package com.agrilink.service;

import com.agrilink.dto.request.CreateCommodityRequest;
import com.agrilink.dto.response.CommodityResponse;

import java.util.List;

public interface CommodityService {

    CommodityResponse createCommodity(CreateCommodityRequest request);

    CommodityResponse getCommodityById(Long id);

    List<CommodityResponse> getAllCommodities();

    List<CommodityResponse> getCommoditiesByCategory(String category);
}
