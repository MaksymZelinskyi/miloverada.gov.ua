package gov.milove.main.controller.integrationtest;

import gov.milove.config.IntegrationTest;
import gov.milove.main.domain.AppUser;
import gov.milove.main.repository.jpa.AppUserRepository;
import gov.milove.main.service.impl.AppUserServiceImpl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.*;

import static org.junit.Assert.assertEquals;

@AutoConfigureMockMvc
class AppUserServiceIntegrationTest extends AuthenticatedIntegrationTest {

    @Autowired private AppUserServiceImpl service;

    @Test
    void getCurrentUserWorksWithSpringSecurity() {
        AppUser current = service.getCurrentUser();

        assertEquals("email@test.com", current.getEmail());
    }
}
