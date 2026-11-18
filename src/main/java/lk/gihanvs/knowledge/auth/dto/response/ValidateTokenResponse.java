package lk.gihanvs.knowledge.auth.dto.response;

import lk.gihanvs.knowledge.auth.entity.Role;
import java.util.UUID;

/**
 * Token validation response for downstream services or API Gateway.
 */
public record ValidateTokenResponse(
        boolean valid,
        UUID userId,
        String email,
        Role role
) {
    public static ValidateTokenResponse invalid() {
        return new ValidateTokenResponse(false, null, null, null);
    }

    public static ValidateTokenResponse valid(UUID userId, String email, Role role) {
        return new ValidateTokenResponse(true, userId, email, role);
    }
}
