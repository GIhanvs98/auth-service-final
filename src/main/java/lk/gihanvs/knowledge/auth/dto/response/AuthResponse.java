package lk.gihanvs.knowledge.auth.dto.response;

import lk.gihanvs.knowledge.auth.entity.Role;
import java.util.UUID;

/**
 * Authentication response containing the JWT bearer token and user metadata.
 */
public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        UUID userId,
        String username,
        String email,
        Role role
) {
    public static AuthResponse of(String token, long expiresIn, UUID userId, String username, String email, Role role) {
        return new AuthResponse(token, "Bearer", expiresIn, userId, username, email, role);
    }
}
