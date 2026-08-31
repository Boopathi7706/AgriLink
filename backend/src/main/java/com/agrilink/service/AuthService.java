package com.agrilink.service;

import com.agrilink.dto.request.LoginRequest;
import com.agrilink.dto.request.RegisterRequest;
import com.agrilink.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
