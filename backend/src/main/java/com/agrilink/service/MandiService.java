package com.agrilink.service;

import com.agrilink.dto.request.CreateMandiRequest;
import com.agrilink.dto.response.MandiResponse;

import java.util.List;

public interface MandiService {

    MandiResponse createMandi(CreateMandiRequest request);

    MandiResponse getMandiById(Long id);

    List<MandiResponse> getAllMandis();

    List<MandiResponse> getMandisByState(String state);

    List<MandiResponse> getMandisByStateAndDistrict(String state, String district);
}
