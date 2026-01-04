package gov.milove.main.service.integrationtest;

import gov.milove.config.IntegrationTest;
import gov.milove.main.domain.AppUser;
import gov.milove.main.repository.jpa.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AuthenticatedIntegrationTest extends IntegrationTest {


    protected final String TEST_EMAIL = "admin@email.com";
    @Autowired
    protected AppUserRepository repo;
    protected AppUser user;

    @BeforeEach
    public void createUser() {
        this.user = repo.save(AppUser.builder().id("user-1").email(TEST_EMAIL).firstName("John").lastName("Doe").avatarUrl("").build());

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "user-1");
        attributes.put("email", TEST_EMAIL);

        // Create OAuth2User with authorities
        OAuth2User oauth2User = new DefaultOAuth2User(Collections.singletonList(new SimpleGrantedAuthority("admin")), attributes, "sub" // name attribute key
        );

        // Create OAuth2AuthenticationToken
        OAuth2AuthenticationToken token = new OAuth2AuthenticationToken(oauth2User, oauth2User.getAuthorities(), "google" // client registration id
        );
        SecurityContextHolder.getContext().setAuthentication(token);
    }
}
