package gov.milove.main.service.unittest;

import gov.milove.main.domain.Document;
import gov.milove.main.domain.AppUser;
import gov.milove.main.dto.DocumentReportItemDto;
import gov.milove.main.repository.jpa.DocumentStatisticsRepository;
import gov.milove.main.service.impl.DocumentReportService;
import gov.milove.main.service.impl.DocumentStatsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
public class ReportTests {

    @Mock
    private DocumentStatisticsRepository repository;
    @InjectMocks
    private DocumentStatsService documentStatsService;

    @BeforeEach
    public void generateMockData() {
        List<Document> mockDocs = List.of(
                Document.builder()
                        .id(1L)
                        .name("Blah-blah-blah")
                        .addedBy(AppUser.builder().email("User1").build())
                        .createdOn(LocalDateTime.now())
                        .build(),
                Document.builder()
                        .id(5L)
                        .name("Blah-blah")
                        .addedBy(AppUser.builder().email("User1").build())
                        .createdOn(LocalDateTime.now())
                        .build()
        );
        List<DocumentReportItemDto> mockStats = mockDocs
                .stream()
                .map(x -> new DocumentReportItemDto(
                        x.getId(), x.getName(), "group1", 100L, 33.5, 50L, 0d, "user1"
                        )
                )
                .toList();

        Mockito.when(repository.findDocumentStatisticsByCreatedOnBetween(any(), any())).thenReturn(mockStats);
    }

    @Test
    public void notEmptyReportIsGenerated() throws Exception {
        List<DocumentReportItemDto> report = documentStatsService.getReportData(LocalDateTime.now(), LocalDateTime.MAX);
        Assertions.assertNotNull(report);
        Assertions.assertFalse(report.isEmpty());
    }

}
