package gov.milove.main.service.integrationtest;

import gov.milove.main.repository.jpa.AppUserRepository;
import gov.milove.main.repository.jpa.DocumentRepository;
import gov.milove.main.repository.jpa.DocumentStatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class AbstractDocumentStatsIntegrationTest extends AuthenticatedIntegrationTest {

    @Autowired
    protected DocumentStatisticsRepository documentStatisticsRepository;

    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private AppUserRepository appUserRepository;


}
