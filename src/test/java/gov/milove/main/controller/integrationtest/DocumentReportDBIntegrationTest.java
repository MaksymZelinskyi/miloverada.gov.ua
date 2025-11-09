package gov.milove.main.controller.integrationtest;

import gov.milove.config.IntegrationTest;
import gov.milove.main.domain.Action;
import gov.milove.main.domain.AppUser;
import gov.milove.main.domain.Document;
import gov.milove.main.domain.DocumentRetrieval;
import gov.milove.main.dto.DocumentReportItemDto;
import gov.milove.main.repository.jpa.AppUserRepository;
import gov.milove.main.repository.jpa.DocumentRepository;
import gov.milove.main.repository.jpa.DocumentStatisticsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Transactional
public class DocumentReportDBIntegrationTest extends IntegrationTest {

    private final int DOWNLOADS_COUNT = 5, VIEWS_COUNT = 10;

    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private DocumentStatisticsRepository documentStatisticsRepository;
    @Autowired
    private AppUserRepository appUserRepository;

    private AppUser user;
    private Document document;

    @BeforeEach
    public void saveAndFetchDocument() {
        user = new AppUser();
        user.setEmail("email");
        user = appUserRepository.save(user);

        document = new Document();
        document.setTitle("Document1");
        document.setName("Document1");
        document.setAddedBy(user);
        document = documentRepository.save(document);

        for (int i = 0; i < DOWNLOADS_COUNT; i++) {
            documentStatisticsRepository.save(new DocumentRetrieval(document, Action.DOWNLOAD));
        }
        for (int i = 0; i < VIEWS_COUNT; i++) {
            documentStatisticsRepository.save(new DocumentRetrieval(document, Action.VIEW));
        }
    }

    @Test
    public void testDocumentRetrievalsRecorded() {
        List<DocumentReportItemDto> list = documentStatisticsRepository.
                findDocumentStatisticsByCreatedOnBetween(LocalDateTime.now().minusDays(1), LocalDateTime.now());
        DocumentReportItemDto dto = list.stream().filter(x -> Objects.equals(x.getId(), document.getId())).findFirst().orElseThrow();

        assertEquals(DOWNLOADS_COUNT, dto.getDownloads());
        assertEquals(VIEWS_COUNT, dto.getViews());
    }
}