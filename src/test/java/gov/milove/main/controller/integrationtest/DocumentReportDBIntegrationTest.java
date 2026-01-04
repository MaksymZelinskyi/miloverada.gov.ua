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
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class DocumentReportDBIntegrationTest extends AbstractDocumentStatsIntegrationTest {

    private final int DOWNLOADS_COUNT = 5, VIEWS_COUNT = 10;

    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private DocumentStatisticsRepository documentStatisticsRepository;

    private LocalDateTime beforeSave;
    private LocalDateTime afterSave;

    @BeforeEach
    public void saveAndFetchDocument() {
        this.beforeSave = LocalDateTime.now().minusMinutes(5);

        for (int i = 0; i < DOWNLOADS_COUNT; i++) {
            DocumentRetrieval dr = documentStatisticsRepository.save(new DocumentRetrieval(testDoc, Action.DOWNLOAD));
            log.info("Recorded document retrieval: {}", dr);
        }
        for (int i = 0; i < VIEWS_COUNT; i++) {
            DocumentRetrieval dr = documentStatisticsRepository.save(new DocumentRetrieval(testDoc, Action.VIEW));
            log.info("Recorded document retrieval: {}", dr);
        }
        this.afterSave = LocalDateTime.now().plusMinutes(1);
    }

    @Test
    public void testDocumentRetrievalsRecorded() {
        List<DocumentReportItemDto> list = documentStatisticsRepository.
                findDocumentStatisticsByCreatedOnBetween(beforeSave, afterSave);
        DocumentReportItemDto dto = list.stream()
                .filter(x -> Objects.equals(x.getId(), testDocumentId))
                .findFirst().orElseThrow();

        assertEquals(DOWNLOADS_COUNT, dto.getDownloads());
        assertEquals(VIEWS_COUNT, dto.getViews());
    }
}