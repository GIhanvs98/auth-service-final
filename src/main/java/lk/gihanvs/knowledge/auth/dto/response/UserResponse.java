package lk.gihanvs.knowledge.auth.dto.response;

import lk.gihanvs.knowledge.auth.entity.Role;
import lk.gihanvs.knowledge.auth.entity.User;

import java.time.Instant;
import java.util.UUID;

/**
 * Public representation of a User entity (passwords excluded).
 */
public record UserResponse(
        UUID id,
        String username,
        String email,
        Role role,
        boolean active,
        Instant createdAt
) {
    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.isActive(),
                user.getCreatedAt()
        );
    }
}
