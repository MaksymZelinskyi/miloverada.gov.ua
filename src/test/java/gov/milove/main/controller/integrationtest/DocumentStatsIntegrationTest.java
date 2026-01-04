package gov.milove.main.controller.integrationtest;

import gov.milove.main.domain.Action;
import gov.milove.main.domain.DocumentRetrieval;
import gov.milove.main.dto.DocumentReportItemDto;
import gov.milove.main.service.impl.DocumentStatsService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class DocumentStatsIntegrationTest extends AbstractDocumentStatsIntegrationTest {

    private final int DOWNLOADS_COUNT = 5, VIEWS_COUNT = 10;

    @Autowired
    private DocumentStatsService documentStatsService;


    @BeforeEach
    public void retrieveDocs() {
        for (int i = 0; i < DOWNLOADS_COUNT; i++) {
            DocumentRetrieval dr = documentStatisticsRepository.save(new DocumentRetrieval(testDoc, Action.DOWNLOAD));
            log.info("Recorded document retrieval: {}", dr);
        }
        for (int i = 0; i < VIEWS_COUNT; i++) {
            DocumentRetrieval dr = documentStatisticsRepository.save(new DocumentRetrieval(testDoc, Action.VIEW));
            log.info("Recorded document retrieval: {}", dr);
        }
    }

    @Test
    void documentAddedToStatistics() {
        List<DocumentReportItemDto> stats =
                documentStatisticsRepository.findDocumentStatisticsByCreatedOnBetween(
                        LocalDateTime.now().minusMonths(1),
                        LocalDateTime.now());

        assertThat(stats).isNotNull().isNotEmpty();
        assertThat(stats.get(0).getTitle()).isEqualTo(TEST_FILENAME);
        assertThat(stats.get(0).getAddedBy()).isEqualTo(TEST_EMAIL);
    }

    @Test
    public void deltasWrittenProperly() {
        List<DocumentReportItemDto> stats = documentStatsService.getReportData(
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now()
        );

        assertEquals(100.0, stats.get(0).getDownloadsDelta());
        assertEquals(100.0, stats.get(0).getViewsDelta());
    }
}
