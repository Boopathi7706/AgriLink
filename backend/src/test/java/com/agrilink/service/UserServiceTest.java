package com.agrilink.service;

import com.agrilink.dto.request.UpdateBuyerProfileRequest;
import com.agrilink.dto.request.UpdateFarmerProfileRequest;
import com.agrilink.dto.response.BuyerProfileResponse;
import com.agrilink.dto.response.FarmerProfileResponse;
import com.agrilink.dto.response.UserResponse;
import com.agrilink.entity.BuyerProfile;
import com.agrilink.entity.FarmerProfile;
import com.agrilink.entity.User;
import com.agrilink.entity.enums.AccountStatus;
import com.agrilink.entity.enums.BuyerType;
import com.agrilink.entity.enums.Role;
import com.agrilink.repository.BuyerProfileRepository;
import com.agrilink.repository.FarmerProfileRepository;
import com.agrilink.repository.UserRepository;
import com.agrilink.security.UserPrincipal;
import com.agrilink.service.impl.UserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FarmerProfileRepository farmerProfileRepository;

    @Mock
    private BuyerProfileRepository buyerProfileRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User farmerUser;
    private User buyerUser;

    @BeforeEach
    void setUp() {
        farmerUser = new User("Ramesh", "ramesh@example.com", "hash", "9876543210", Role.FARMER, AccountStatus.ACTIVE);
        farmerUser.setId(1L);
        FarmerProfile fp = new FarmerProfile(farmerUser, new BigDecimal("5.0"), "Village A", "Dindigul", "Tamil Nadu", "624001", "Tomato");
        fp.setId(10L);
        farmerUser.setFarmerProfile(fp);

        buyerUser = new User("Suresh", "suresh@example.com", "hash", "9876543211", Role.BUYER, AccountStatus.ACTIVE);
        buyerUser.setId(2L);
        BuyerProfile bp = new BuyerProfile(buyerUser, "Agro Corp", BuyerType.WHOLESALER, "GSTIN123", "Address", "Chennai", "Tamil Nadu", "600001");
        bp.setId(20L);
        buyerUser.setBuyerProfile(bp);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateUser(User user) {
        UserPrincipal principal = UserPrincipal.create(user);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("getCurrentUser returns authenticated user details")
    void getCurrentUser_Success() {
        authenticateUser(farmerUser);
        when(userRepository.findById(1L)).thenReturn(Optional.of(farmerUser));

        UserResponse response = userService.getCurrentUser();

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Ramesh");
        assertThat(response.getFarmerProfile()).isNotNull();
    }

    @Test
    @DisplayName("updateFarmerProfile succeeds for FARMER")
    void updateFarmerProfile_Success() {
        authenticateUser(farmerUser);
        when(userRepository.findById(1L)).thenReturn(Optional.of(farmerUser));
        when(farmerProfileRepository.findByUserId(1L)).thenReturn(Optional.of(farmerUser.getFarmerProfile()));
        when(farmerProfileRepository.save(any(FarmerProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateFarmerProfileRequest request = new UpdateFarmerProfileRequest(
                new BigDecimal("8.5"), "New Village", "Madurai", "Tamil Nadu", "625001", "Chilli, Tomato"
        );

        FarmerProfileResponse response = userService.updateFarmerProfile(request);

        assertThat(response).isNotNull();
        assertThat(response.getFarmSizeAcres()).isEqualTo(new BigDecimal("8.5"));
        assertThat(response.getDistrict()).isEqualTo("Madurai");
    }

    @Test
    @DisplayName("updateFarmerProfile throws AccessDeniedException when caller is BUYER")
    void updateFarmerProfile_NonFarmer_ThrowsAccessDenied() {
        authenticateUser(buyerUser);
        when(userRepository.findById(2L)).thenReturn(Optional.of(buyerUser));

        UpdateFarmerProfileRequest request = new UpdateFarmerProfileRequest(
                new BigDecimal("8.5"), "New Village", "Madurai", "Tamil Nadu", "625001", "Chilli, Tomato"
        );

        assertThatThrownBy(() -> userService.updateFarmerProfile(request))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Only farmers can update a farmer profile");
    }

    @Test
    @DisplayName("updateBuyerProfile succeeds for BUYER")
    void updateBuyerProfile_Success() {
        authenticateUser(buyerUser);
        when(userRepository.findById(2L)).thenReturn(Optional.of(buyerUser));
        when(buyerProfileRepository.findByUserId(2L)).thenReturn(Optional.of(buyerUser.getBuyerProfile()));
        when(buyerProfileRepository.save(any(BuyerProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateBuyerProfileRequest request = new UpdateBuyerProfileRequest(
                "Fresh Retailers Ltd", BuyerType.RETAILER, "GSTIN999", "New Address", "Coimbatore", "Tamil Nadu", "641001"
        );

        BuyerProfileResponse response = userService.updateBuyerProfile(request);

        assertThat(response).isNotNull();
        assertThat(response.getBusinessName()).isEqualTo("Fresh Retailers Ltd");
        assertThat(response.getBuyerType()).isEqualTo(BuyerType.RETAILER);
    }

    @Test
    @DisplayName("updateBuyerProfile throws AccessDeniedException when caller is FARMER")
    void updateBuyerProfile_NonBuyer_ThrowsAccessDenied() {
        authenticateUser(farmerUser);
        when(userRepository.findById(1L)).thenReturn(Optional.of(farmerUser));

        UpdateBuyerProfileRequest request = new UpdateBuyerProfileRequest(
                "Fresh Retailers Ltd", BuyerType.RETAILER, "GSTIN999", "New Address", "Coimbatore", "Tamil Nadu", "641001"
        );

        assertThatThrownBy(() -> userService.updateBuyerProfile(request))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Only buyers can update a buyer profile");
    }
}
