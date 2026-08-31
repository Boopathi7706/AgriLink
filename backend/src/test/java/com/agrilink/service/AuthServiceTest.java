package com.agrilink.service;

import com.agrilink.dto.request.BuyerProfileRequest;
import com.agrilink.dto.request.FarmerProfileRequest;
import com.agrilink.dto.request.LoginRequest;
import com.agrilink.dto.request.RegisterRequest;
import com.agrilink.dto.response.AuthResponse;
import com.agrilink.entity.FarmerProfile;
import com.agrilink.entity.User;
import com.agrilink.entity.enums.AccountStatus;
import com.agrilink.entity.enums.BuyerType;
import com.agrilink.entity.enums.Role;
import com.agrilink.exception.AccountDisabledException;
import com.agrilink.exception.DuplicateResourceException;
import com.agrilink.repository.UserRepository;
import com.agrilink.security.JwtService;
import com.agrilink.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest farmerRegisterRequest;
    private RegisterRequest buyerRegisterRequest;

    @BeforeEach
    void setUp() {
        FarmerProfileRequest fpReq = new FarmerProfileRequest(
                new BigDecimal("5.0"), "Village A", "Dindigul", "Tamil Nadu", "624001", "Tomato, Onion"
        );
        farmerRegisterRequest = new RegisterRequest(
                "Ramesh Farmer", "ramesh@example.com", "Password@123", "9876543210", Role.FARMER, fpReq, null
        );

        BuyerProfileRequest bpReq = new BuyerProfileRequest(
                "Fresh Agro Traders", BuyerType.WHOLESALER, "33AAAAA0000A1Z5", "123 Market St", "Chennai", "Tamil Nadu", "600001"
        );
        buyerRegisterRequest = new RegisterRequest(
                "Suresh Buyer", "suresh@example.com", "Password@123", "9876543211", Role.BUYER, null, bpReq
        );
    }

    @Test
    @DisplayName("Farmer registration success")
    void register_Farmer_Success() {
        when(userRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedHash");
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");
        when(jwtService.getExpirationSeconds()).thenReturn(86400L);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            if (u.getFarmerProfile() != null) {
                u.getFarmerProfile().setId(10L);
            }
            return u;
        });

        AuthResponse response = authService.register(farmerRegisterRequest);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("jwt-token");
        assertThat(response.getUser().getId()).isEqualTo(1L);
        assertThat(response.getUser().getRole()).isEqualTo(Role.FARMER);
        assertThat(response.getUser().getFarmerProfile()).isNotNull();
        assertThat(response.getUser().getFarmerProfile().getDistrict()).isEqualTo("Dindigul");
    }

    @Test
    @DisplayName("Buyer registration success")
    void register_Buyer_Success() {
        when(userRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedHash");
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");
        when(jwtService.getExpirationSeconds()).thenReturn(86400L);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(2L);
            if (u.getBuyerProfile() != null) {
                u.getBuyerProfile().setId(20L);
            }
            return u;
        });

        AuthResponse response = authService.register(buyerRegisterRequest);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("jwt-token");
        assertThat(response.getUser().getRole()).isEqualTo(Role.BUYER);
        assertThat(response.getUser().getBuyerProfile()).isNotNull();
        assertThat(response.getUser().getBuyerProfile().getBusinessName()).isEqualTo("Fresh Agro Traders");
    }

    @Test
    @DisplayName("Admin registration is rejected")
    void register_Admin_ThrowsException() {
        RegisterRequest adminRequest = new RegisterRequest(
                "Admin User", "admin@agrilink.com", "AdminPass@123", "9999999999", Role.ADMIN, null, null
        );

        assertThatThrownBy(() -> authService.register(adminRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Admin registration is not permitted");
    }

    @Test
    @DisplayName("Farmer registration without farmerProfile throws exception")
    void register_Farmer_MissingProfile_ThrowsException() {
        RegisterRequest invalid = new RegisterRequest(
                "Ramesh", "ramesh@example.com", "Password@123", "9876543210", Role.FARMER, null, null
        );

        assertThatThrownBy(() -> authService.register(invalid))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Farmer profile details are required");
    }

    @Test
    @DisplayName("Buyer registration without buyerProfile throws exception")
    void register_Buyer_MissingProfile_ThrowsException() {
        RegisterRequest invalid = new RegisterRequest(
                "Suresh", "suresh@example.com", "Password@123", "9876543211", Role.BUYER, null, null
        );

        assertThatThrownBy(() -> authService.register(invalid))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Buyer profile details are required");
    }

    @Test
    @DisplayName("Duplicate email throws DuplicateResourceException")
    void register_DuplicateEmail_ThrowsException() {
        when(userRepository.existsByEmailIgnoreCase("ramesh@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(farmerRegisterRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("email 'ramesh@example.com' already exists");
    }

    @Test
    @DisplayName("Duplicate phone throws DuplicateResourceException")
    void register_DuplicatePhone_ThrowsException() {
        when(userRepository.existsByEmailIgnoreCase("ramesh@example.com")).thenReturn(false);
        when(userRepository.existsByPhoneNumber("9876543210")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(farmerRegisterRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("phone number '9876543210' already exists");
    }

    @Test
    @DisplayName("Successful login returns AuthResponse")
    void login_Success() {
        User user = new User("Ramesh", "ramesh@example.com", "encodedHash", "9876543210", Role.FARMER, AccountStatus.ACTIVE);
        user.setId(1L);

        when(userRepository.findByEmailIgnoreCase("ramesh@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password@123", "encodedHash")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");
        when(jwtService.getExpirationSeconds()).thenReturn(86400L);

        AuthResponse response = authService.login(new LoginRequest("ramesh@example.com", "Password@123"));

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("jwt-token");
        assertThat(response.getUser().getEmail()).isEqualTo("ramesh@example.com");
    }

    @Test
    @DisplayName("Login with unknown email throws BadCredentialsException")
    void login_UnknownEmail_ThrowsBadCredentials() {
        when(userRepository.findByEmailIgnoreCase("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest("unknown@example.com", "Password@123")))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid email or password");
    }

    @Test
    @DisplayName("Login with wrong password throws BadCredentialsException")
    void login_WrongPassword_ThrowsBadCredentials() {
        User user = new User("Ramesh", "ramesh@example.com", "encodedHash", "9876543210", Role.FARMER, AccountStatus.ACTIVE);

        when(userRepository.findByEmailIgnoreCase("ramesh@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPass", "encodedHash")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("ramesh@example.com", "WrongPass")))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid email or password");
    }

    @Test
    @DisplayName("Login for suspended account throws AccountDisabledException")
    void login_SuspendedAccount_ThrowsAccountDisabledException() {
        User user = new User("Ramesh", "ramesh@example.com", "encodedHash", "9876543210", Role.FARMER, AccountStatus.SUSPENDED);

        when(userRepository.findByEmailIgnoreCase("ramesh@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password@123", "encodedHash")).thenReturn(true);

        assertThatThrownBy(() -> authService.login(new LoginRequest("ramesh@example.com", "Password@123")))
                .isInstanceOf(AccountDisabledException.class)
                .hasMessageContaining("suspended or deactivated");
    }
}
