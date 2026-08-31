package com.agrilink.service;

import com.agrilink.dto.request.UpdateBuyerProfileRequest;
import com.agrilink.dto.request.UpdateFarmerProfileRequest;
import com.agrilink.dto.response.BuyerProfileResponse;
import com.agrilink.dto.response.FarmerProfileResponse;
import com.agrilink.dto.response.UserResponse;

public interface UserService {

    UserResponse getCurrentUser();

    FarmerProfileResponse updateFarmerProfile(UpdateFarmerProfileRequest request);

    BuyerProfileResponse updateBuyerProfile(UpdateBuyerProfileRequest request);
}
