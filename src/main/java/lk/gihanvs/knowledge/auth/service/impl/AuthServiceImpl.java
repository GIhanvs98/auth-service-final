package lk.gihanvs.knowledge.auth.service.impl;

import lk.gihanvs.knowledge.auth.dto.request.LoginRequest;
import lk.gihanvs.knowledge.auth.dto.request.RegisterRequest;
import lk.gihanvs.knowledge.auth.dto.response.AuthResponse;
import lk.gihanvs.knowledge.auth.dto.response.UserResponse;
import lk.gihanvs.knowledge.auth.dto.response.ValidateTokenResponse;
import lk.gihanvs.knowledge.auth.entity.Role;
import lk.gihanvs.knowledge.auth.entity.User;
import lk.gihanvs.knowledge.auth.exception.ResourceNotFoundException;
import lk.gihanvs.knowledge.auth.exception.UserAlreadyExistsException;
import lk.gihanvs.knowledge.auth.repository.UserRepository;
import lk.gihanvs.knowledge.auth.service.AuthService;
import lk.gihanvs.knowledge.auth.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementation of AuthService managing user registration, credential validation,
 * and JWT lifecycle operations.
 */
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("A user with email '" + request.email() + "' already exists");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException("A user with username '" + request.username() + "' already exists");
        }

        Role assignedRole = request.role() != null ? request.role() : Role.ROLE_USER;
        String hashedPassword = passwordEncoder.encode(request.password());

        User user = new User(request.username(), request.email(), hashedPassword, assignedRole);
        User savedUser = userRepository.save(user);

        log.info("Registered new user: {} with role: {}", savedUser.getEmail(), savedUser.getRole());

        String token = jwtService.generateToken(savedUser);
        return AuthResponse.of(token, jwtService.getExpirationMs(), savedUser.getId(), savedUser.getUsername(), savedUser.getEmail(), savedUser.getRole());
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        if (!user.isActive()) {
            throw new BadCredentialsException("Account has been deactivated. Please contact an administrator.");
        }

        log.info("User logged in successfully: {}", user.getEmail());

        String token = jwtService.generateToken(user);
        return AuthResponse.of(token, jwtService.getExpirationMs(), user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }

    @Override
    @Transactional(readOnly = true)
    public ValidateTokenResponse validateToken(String token) {
        if (token == null || !jwtService.isTokenValid(token)) {
            return ValidateTokenResponse.invalid();
        }

        UUID userId = jwtService.extractUserId(token);
        String email = jwtService.extractEmail(token);
        String roleStr = jwtService.extractRole(token);
        Role role = roleStr != null ? Role.valueOf(roleStr) : Role.ROLE_USER;

        return ValidateTokenResponse.valid(userId, email, role);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        return UserResponse.fromEntity(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return UserResponse.fromEntity(user);
    }
}
