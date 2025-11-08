package gov.milove.main.controller.integrationtest;

import gov.milove.config.IntegrationTest;
import gov.milove.main.domain.AppUser;
import gov.milove.main.repository.jpa.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;

public class AuthenticatedIntegrationTest extends IntegrationTest {


    @Autowired
    protected AppUserRepository repo;

    protected AppUser user;
    protected final String TEST_EMAIL = "admin@email.com";

    @BeforeEach
    public void createUser() {
         user = repo.save(AppUser.builder()
                 .id("user-1")
                 .email(TEST_EMAIL)
                 .firstName("John")
                 .lastName("Doe")
                 .avatarUrl("")
                 .build());

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "user-1")
                .claim("email", TEST_EMAIL)
                .claim("permissions", List.of("admin"))
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new JwtAuthenticationToken(jwt)
        );
    }
}
