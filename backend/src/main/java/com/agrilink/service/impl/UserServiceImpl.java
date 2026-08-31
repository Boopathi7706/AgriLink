package com.agrilink.service.impl;

import com.agrilink.dto.request.UpdateBuyerProfileRequest;
import com.agrilink.dto.request.UpdateFarmerProfileRequest;
import com.agrilink.dto.response.BuyerProfileResponse;
import com.agrilink.dto.response.FarmerProfileResponse;
import com.agrilink.dto.response.UserResponse;
import com.agrilink.entity.BuyerProfile;
import com.agrilink.entity.FarmerProfile;
import com.agrilink.entity.User;
import com.agrilink.entity.enums.Role;
import com.agrilink.exception.ResourceNotFoundException;
import com.agrilink.repository.BuyerProfileRepository;
import com.agrilink.repository.FarmerProfileRepository;
import com.agrilink.repository.UserRepository;
import com.agrilink.service.UserService;
import com.agrilink.util.SecurityUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FarmerProfileRepository farmerProfileRepository;
    private final BuyerProfileRepository buyerProfileRepository;

    public UserServiceImpl(
            UserRepository userRepository,
            FarmerProfileRepository farmerProfileRepository,
            BuyerProfileRepository buyerProfileRepository
    ) {
        this.userRepository = userRepository;
        this.farmerProfileRepository = farmerProfileRepository;
        this.buyerProfileRepository = buyerProfileRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return AuthServiceImpl.mapToUserResponse(user);
    }

    @Override
    @Transactional
    public FarmerProfileResponse updateFarmerProfile(UpdateFarmerProfileRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (user.getRole() != Role.FARMER) {
            throw new AccessDeniedException("Only farmers can update a farmer profile");
        }

        FarmerProfile profile = farmerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer profile not found for user: " + userId));

        profile.setFarmSizeAcres(request.getFarmSizeAcres());
        profile.setVillage(request.getVillage());
        profile.setDistrict(request.getDistrict().trim());
        profile.setState(request.getState().trim());
        profile.setPincode(request.getPincode());
        profile.setPrimaryCrops(request.getPrimaryCrops());

        FarmerProfile saved = farmerProfileRepository.save(profile);

        return new FarmerProfileResponse(
                saved.getId(),
                saved.getFarmSizeAcres(),
                saved.getVillage(),
                saved.getDistrict(),
                saved.getState(),
                saved.getPincode(),
                saved.getPrimaryCrops(),
                saved.isVerified(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }

    @Override
    @Transactional
    public BuyerProfileResponse updateBuyerProfile(UpdateBuyerProfileRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (user.getRole() != Role.BUYER) {
            throw new AccessDeniedException("Only buyers can update a buyer profile");
        }

        BuyerProfile profile = buyerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Buyer profile not found for user: " + userId));

        profile.setBusinessName(request.getBusinessName().trim());
        profile.setBuyerType(request.getBuyerType());
        profile.setGstin(request.getGstin());
        profile.setAddress(request.getAddress());
        profile.setDistrict(request.getDistrict().trim());
        profile.setState(request.getState().trim());
        profile.setPincode(request.getPincode());

        BuyerProfile saved = buyerProfileRepository.save(profile);

        return new BuyerProfileResponse(
                saved.getId(),
                saved.getBusinessName(),
                saved.getBuyerType(),
                saved.getGstin(),
                saved.getAddress(),
                saved.getDistrict(),
                saved.getState(),
                saved.getPincode(),
                saved.isVerified(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }
}
