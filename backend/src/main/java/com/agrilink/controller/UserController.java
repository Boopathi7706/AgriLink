package com.agrilink.controller;

import com.agrilink.dto.request.UpdateBuyerProfileRequest;
import com.agrilink.dto.request.UpdateFarmerProfileRequest;
import com.agrilink.dto.response.BuyerProfileResponse;
import com.agrilink.dto.response.FarmerProfileResponse;
import com.agrilink.dto.response.UserResponse;
import com.agrilink.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        UserResponse response = userService.getCurrentUser();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me/farmer-profile")
    public ResponseEntity<FarmerProfileResponse> updateFarmerProfile(@Valid @RequestBody UpdateFarmerProfileRequest request) {
        FarmerProfileResponse response = userService.updateFarmerProfile(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me/buyer-profile")
    public ResponseEntity<BuyerProfileResponse> updateBuyerProfile(@Valid @RequestBody UpdateBuyerProfileRequest request) {
        BuyerProfileResponse response = userService.updateBuyerProfile(request);
        return ResponseEntity.ok(response);
    }
}
