package com.agrilink.service.impl;

import com.agrilink.dto.request.CreateMandiRequest;
import com.agrilink.dto.response.MandiResponse;
import com.agrilink.entity.Mandi;
import com.agrilink.exception.DuplicateResourceException;
import com.agrilink.exception.ResourceNotFoundException;
import com.agrilink.repository.MandiRepository;
import com.agrilink.service.MandiService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MandiServiceImpl implements MandiService {

    private final MandiRepository mandiRepository;

    public MandiServiceImpl(MandiRepository mandiRepository) {
        this.mandiRepository = mandiRepository;
    }

    @Override
    @Transactional
    public MandiResponse createMandi(CreateMandiRequest request) {
        String trimmedName = request.getName().trim();
        String trimmedDistrict = request.getDistrict().trim();
        String trimmedState = request.getState().trim();

        if (mandiRepository.existsByNameIgnoreCaseAndDistrictIgnoreCaseAndStateIgnoreCase(trimmedName, trimmedDistrict, trimmedState)) {
            throw new DuplicateResourceException("Mandi '" + trimmedName + "' in district '" + trimmedDistrict + "', state '" + trimmedState + "' already exists");
        }

        Mandi mandi = new Mandi(
                trimmedName,
                trimmedDistrict,
                trimmedState,
                request.getLatitude(),
                request.getLongitude()
        );

        Mandi savedMandi = mandiRepository.save(mandi);
        return toMandiResponse(savedMandi);
    }

    @Override
    public MandiResponse getMandiById(Long id) {
        Mandi mandi = mandiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mandi not found with id: " + id));
        return toMandiResponse(mandi);
    }

    @Override
    public List<MandiResponse> getAllMandis() {
        return mandiRepository.findAllByOrderByNameAsc()
                .stream()
                .map(this::toMandiResponse)
                .toList();
    }

    @Override
    public List<MandiResponse> getMandisByState(String state) {
        return mandiRepository.findByStateIgnoreCase(state.trim())
                .stream()
                .map(this::toMandiResponse)
                .toList();
    }

    @Override
    public List<MandiResponse> getMandisByStateAndDistrict(String state, String district) {
        return mandiRepository.findByStateIgnoreCaseAndDistrictIgnoreCase(state.trim(), district.trim())
                .stream()
                .map(this::toMandiResponse)
                .toList();
    }

    private MandiResponse toMandiResponse(Mandi mandi) {
        return new MandiResponse(
                mandi.getId(),
                mandi.getName(),
                mandi.getDistrict(),
                mandi.getState(),
                mandi.getLatitude(),
                mandi.getLongitude(),
                mandi.getCreatedAt(),
                mandi.getUpdatedAt()
        );
    }
}
