package gov.milove.main.controller.integrationtest;

import gov.milove.config.IntegrationTest;
import gov.milove.main.domain.AppUser;
import gov.milove.main.repository.jpa.AppUserRepository;
import gov.milove.main.service.impl.AppUserServiceImpl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;

import static org.junit.Assert.assertEquals;

@AutoConfigureMockMvc
class AppUserServiceIntegrationTest extends IntegrationTest {

    @Autowired private AppUserRepository repo;
    @Autowired private AppUserServiceImpl service;

    @Test
    void getCurrentUserWorksWithSpringSecurity() {
        AppUser user = repo.save(AppUser.builder()
                .id("user-1")
                .email("email@test.com")
                .firstName("John")
                .lastName("Doe")
                .avatarUrl("")
                .build());

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "user-1")
                .claim("email", "email@test.com")
                .claim("permissions", List.of("admin"))
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new JwtAuthenticationToken(jwt)
        );

        AppUser current = service.getCurrentUser();

        assertEquals("email@test.com", current.getEmail());
    }
}
