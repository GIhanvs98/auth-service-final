package lk.gihanvs.knowledge.auth.service;

import lk.gihanvs.knowledge.auth.dto.request.LoginRequest;
import lk.gihanvs.knowledge.auth.dto.request.RegisterRequest;
import lk.gihanvs.knowledge.auth.dto.response.AuthResponse;
import lk.gihanvs.knowledge.auth.dto.response.UserResponse;
import lk.gihanvs.knowledge.auth.dto.response.ValidateTokenResponse;

import java.util.UUID;

/**
 * Service contract for user authentication, registration, and session token validation.
 */
public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    ValidateTokenResponse validateToken(String token);

    UserResponse getUserById(UUID userId);

    UserResponse getUserByEmail(String email);
}
