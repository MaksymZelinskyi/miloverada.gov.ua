package gov.milove.main.controller.integrationtest;

import gov.milove.config.IntegrationTest;
import gov.milove.config.MainOnlyTestConfig;
import gov.milove.main.domain.AppUser;
import gov.milove.main.repository.jpa.AppUserRepository;
import gov.milove.main.service.impl.AppUserServiceImpl;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest
public class CurrentUserIntegrationTest {

    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private AppUserServiceImpl appUserService;
    @MockBean
    private JwtDecoder jwtDecoder;

    private final String USER_EMAIL = "mockuser@email.com";

    @Before
    public void registerUser(){
        appUserRepository.deleteAllByEmail(USER_EMAIL);
        AppUser appUser = new AppUser();
        appUser.setId(USER_EMAIL);
        appUser.setEmail(USER_EMAIL);

        appUser.setFirstName("John");
        appUser.setLastName("Doe");
        appUser.setAvatarUrl("");

        appUser = appUserRepository.save(appUser);
    }

    @Test
    @WithMockUser(username = USER_EMAIL)
    public void testFindCurrentUser() {
        appUserRepository.findByEmail(USER_EMAIL);
        AppUser currentUser = appUserService.getCurrentUser();
        assertNotNull(currentUser);
        assertEquals(USER_EMAIL, currentUser.getEmail());
    }

}