package gov.milove.main.controller.integrationtest;

import gov.milove.main.dto.DocumentReportItemDto;
import gov.milove.main.service.impl.DocumentStatsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


class DocumentStatsIntegrationTest extends AbstractDocumentStatsIntegrationTest {

    @Autowired
    private DocumentStatsService documentStatsService;

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
