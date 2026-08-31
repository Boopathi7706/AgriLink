package com.agrilink.service.impl;

import com.agrilink.dto.request.BuyerProfileRequest;
import com.agrilink.dto.request.FarmerProfileRequest;
import com.agrilink.dto.request.LoginRequest;
import com.agrilink.dto.request.RegisterRequest;
import com.agrilink.dto.response.AuthResponse;
import com.agrilink.dto.response.BuyerProfileResponse;
import com.agrilink.dto.response.FarmerProfileResponse;
import com.agrilink.dto.response.UserResponse;
import com.agrilink.entity.BuyerProfile;
import com.agrilink.entity.FarmerProfile;
import com.agrilink.entity.User;
import com.agrilink.entity.enums.AccountStatus;
import com.agrilink.entity.enums.Role;
import com.agrilink.exception.AccountDisabledException;
import com.agrilink.exception.DuplicateResourceException;
import com.agrilink.repository.UserRepository;
import com.agrilink.security.JwtService;
import com.agrilink.service.AuthService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 1. Role validation
        if (request.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("Admin registration is not permitted via public registration");
        }

        if (request.getRole() == Role.FARMER) {
            if (request.getFarmerProfile() == null) {
                throw new IllegalArgumentException("Farmer profile details are required for FARMER registration");
            }
        } else if (request.getRole() == Role.BUYER) {
            if (request.getBuyerProfile() == null) {
                throw new IllegalArgumentException("Buyer profile details are required for BUYER registration");
            }
        }

        // 2. Uniqueness checks
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new DuplicateResourceException(String.format("User with email '%s' already exists", request.getEmail()));
        }

        String normalizedPhone = request.getPhoneNumber().trim();
        if (userRepository.existsByPhoneNumber(normalizedPhone)) {
            throw new DuplicateResourceException(String.format("User with phone number '%s' already exists", request.getPhoneNumber()));
        }

        // 3. Password encoding
        String passwordHash = passwordEncoder.encode(request.getPassword());

        // 4. Create User entity
        User user = new User(
                request.getName().trim(),
                normalizedEmail,
                passwordHash,
                normalizedPhone,
                request.getRole(),
                AccountStatus.ACTIVE
        );

        // 5. Create Profile entity
        if (request.getRole() == Role.FARMER) {
            FarmerProfileRequest fpReq = request.getFarmerProfile();
            FarmerProfile farmerProfile = new FarmerProfile(
                    user,
                    fpReq.getFarmSizeAcres(),
                    fpReq.getVillage(),
                    fpReq.getDistrict().trim(),
                    fpReq.getState().trim(),
                    fpReq.getPincode(),
                    fpReq.getPrimaryCrops()
            );
            user.setFarmerProfile(farmerProfile);
        } else if (request.getRole() == Role.BUYER) {
            BuyerProfileRequest bpReq = request.getBuyerProfile();
            BuyerProfile buyerProfile = new BuyerProfile(
                    user,
                    bpReq.getBusinessName().trim(),
                    bpReq.getBuyerType(),
                    bpReq.getGstin(),
                    bpReq.getAddress(),
                    bpReq.getDistrict().trim(),
                    bpReq.getState().trim(),
                    bpReq.getPincode()
            );
            user.setBuyerProfile(buyerProfile);
        }

        // 6. Atomically persist user and cascade profile
        User savedUser = userRepository.save(user);

        // 7. Generate JWT
        String token = jwtService.generateToken(savedUser);

        // 8. Return AuthResponse
        return new AuthResponse(token, jwtService.getExpirationSeconds(), mapToUserResponse(savedUser));
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        if (user.getStatus() == AccountStatus.SUSPENDED || user.getStatus() == AccountStatus.DEACTIVATED) {
            throw new AccountDisabledException("Account is suspended or deactivated. Please contact support.");
        }

        String token = jwtService.generateToken(user);

        return new AuthResponse(token, jwtService.getExpirationSeconds(), mapToUserResponse(user));
    }

    public static UserResponse mapToUserResponse(User user) {
        FarmerProfileResponse farmerProfileResponse = null;
        if (user.getFarmerProfile() != null) {
            FarmerProfile fp = user.getFarmerProfile();
            farmerProfileResponse = new FarmerProfileResponse(
                    fp.getId(),
                    fp.getFarmSizeAcres(),
                    fp.getVillage(),
                    fp.getDistrict(),
                    fp.getState(),
                    fp.getPincode(),
                    fp.getPrimaryCrops(),
                    fp.isVerified(),
                    fp.getCreatedAt(),
                    fp.getUpdatedAt()
            );
        }

        BuyerProfileResponse buyerProfileResponse = null;
        if (user.getBuyerProfile() != null) {
            BuyerProfile bp = user.getBuyerProfile();
            buyerProfileResponse = new BuyerProfileResponse(
                    bp.getId(),
                    bp.getBusinessName(),
                    bp.getBuyerType(),
                    bp.getGstin(),
                    bp.getAddress(),
                    bp.getDistrict(),
                    bp.getState(),
                    bp.getPincode(),
                    bp.isVerified(),
                    bp.getCreatedAt(),
                    bp.getUpdatedAt()
            );
        }

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getStatus(),
                farmerProfileResponse,
                buyerProfileResponse,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
