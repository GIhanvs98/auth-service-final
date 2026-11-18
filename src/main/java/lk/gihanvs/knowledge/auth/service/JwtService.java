package lk.gihanvs.knowledge.auth.service;

import io.jsonwebtoken.Claims;
import lk.gihanvs.knowledge.auth.entity.User;

import java.util.UUID;

/**
 * Service contract for JSON Web Token (JWT) generation, validation, and claim parsing.
 */
public interface JwtService {

    String generateToken(User user);

    boolean isTokenValid(String token);

    String extractEmail(String token);

    UUID extractUserId(String token);

    String extractRole(String token);

    Claims extractAllClaims(String token);

    long getExpirationMs();
}
