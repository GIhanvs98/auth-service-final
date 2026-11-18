package lk.gihanvs.knowledge.auth;

import lk.gihanvs.knowledge.auth.dto.request.LoginRequest;
import lk.gihanvs.knowledge.auth.dto.request.RegisterRequest;
import lk.gihanvs.knowledge.auth.dto.response.AuthResponse;
import lk.gihanvs.knowledge.auth.dto.response.ValidateTokenResponse;
import lk.gihanvs.knowledge.auth.entity.Role;
import lk.gihanvs.knowledge.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Startup and core authentication verification tests for AuthService.
 */
@SpringBootTest
class AuthServiceApplicationTests {

    @Autowired
    private AuthService authService;

    @Test
    void contextLoads() {
        assertNotNull(authService, "AuthService bean should be loaded into application context");
    }

    @Test
    void testRegisterAndLoginFlow() {
        RegisterRequest registerRequest = new RegisterRequest(
                "testworker",
                "worker@knowledge.lk",
                "StrongP@ssw0rd!",
                Role.ROLE_KNOWLEDGE_WORKER
        );

        AuthResponse registerResponse = authService.register(registerRequest);
        assertNotNull(registerResponse.accessToken(), "Access token should not be null upon registration");
        assertEquals("worker@knowledge.lk", registerResponse.email());
        assertEquals(Role.ROLE_KNOWLEDGE_WORKER, registerResponse.role());

        LoginRequest loginRequest = new LoginRequest(
                "worker@knowledge.lk",
                "StrongP@ssw0rd!"
        );
        AuthResponse loginResponse = authService.login(loginRequest);
        assertNotNull(loginResponse.accessToken(), "Access token should not be null upon login");

        ValidateTokenResponse validateResponse = authService.validateToken(loginResponse.accessToken());
        assertTrue(validateResponse.valid(), "Token should be valid");
        assertEquals("worker@knowledge.lk", validateResponse.email());
        assertEquals(Role.ROLE_KNOWLEDGE_WORKER, validateResponse.role());
    }
}
