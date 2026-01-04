package gov.milove.main.service.integrationtest;

import gov.milove.main.domain.AppUser;
import gov.milove.main.service.impl.AppUserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.junit.Assert.assertEquals;

@AutoConfigureMockMvc
class AppUserServiceIntegrationTest extends AuthenticatedIntegrationTest {

    @Autowired
    private AppUserServiceImpl service;

    @Test
    void getCurrentUserWorksWithSpringSecurity() {
        AppUser current = service.getCurrentUser();

        assertEquals(TEST_EMAIL, current.getEmail());
    }
}
