package gov.milove.main.controller.integrationtest;

import gov.milove.main.controller.impl.DocumentControllerImpl;
import gov.milove.main.controller.impl.UploadImpl;
import gov.milove.main.dto.DocumentReportItemDto;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class DocumentControllerIntegrationTest extends AbstractDocumentStatsIntegrationTest {

    @Autowired private DocumentControllerImpl documentController;
    @Autowired private UploadImpl upload;

    @Test
    void viewsAndDownloadsAreCounted() throws Exception {
        documentController.markAsViewed(testDocumentId);
        upload.findDocumentByFilename(TEST_FILENAME, mock(HttpServletResponse.class));

        List<DocumentReportItemDto> stats =
                documentStatisticsRepository.findDocumentStatisticsByCreatedOnBetween(
                        LocalDateTime.now().minusMonths(1),
                        LocalDateTime.now());

        assertThat(stats).isNotNull().isNotEmpty();
        assertThat(stats.get(0).getViews()).isGreaterThan(0);
        assertThat(stats.get(0).getDownloads()).isGreaterThan(0);
    }
}
