package gov.milove.main.service.unittest;

import gov.milove.main.dto.DocumentReportItemDto;
import gov.milove.main.repository.jpa.DocumentStatisticsRepository;
import gov.milove.main.service.impl.DocumentStatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DocumentStatsTests {

    @Mock
    private DocumentStatisticsRepository repository;
    @InjectMocks
    private DocumentStatsService documentReportService;

    private final LocalDateTime now = LocalDateTime.of(2025, 10, 24, 16, 6, 59);
    private final List<DocumentReportItemDto> currPeriod = new ArrayList<>();
    private final List<DocumentReportItemDto> prevPeriod = new ArrayList<>();

    @BeforeEach
    public void generateMockData() throws Exception {
        prevPeriod.add(new DocumentReportItemDto(0L, "Title", "Group1", 50L, 50L, "Maksym"));
        prevPeriod.add(new DocumentReportItemDto(1L, "Title", "Group1", 50L, 50L, "Maksym"));
        prevPeriod.add(new DocumentReportItemDto(2L, "Title", "Group1", 50L, 50L, "Maksym"));
        prevPeriod.add(new DocumentReportItemDto(3L, "Title", "Group1", 50L, 50L, "Maksym"));
        prevPeriod.add(new DocumentReportItemDto(4L, "Title", "Group1", 0L, 0L, "Maksym"));
        prevPeriod.add(new DocumentReportItemDto(5L, "Title", "Group1", 0L, 0L, "Maksym"));

        currPeriod.add(new DocumentReportItemDto(0L, "Title", "Group1", 65L, 80L, "Maksym"));
        currPeriod.add(new DocumentReportItemDto(1L, "Title", "Group1", 40L, 15L, "Maksym"));
        currPeriod.add(new DocumentReportItemDto(2L, "Title", "Group1", 50L, 50L, "Maksym"));
        currPeriod.add(new DocumentReportItemDto(3L, "Title", "Group1", 0L, 0L, "Maksym"));
        currPeriod.add(new DocumentReportItemDto(4L, "Title", "Group1", 0L, 0L, "Maksym"));
        currPeriod.add(new DocumentReportItemDto(5L, "Title", "Group1", 100L, 100L, "Maksym"));
    }

    @Test
    public void viewsDeltaComputedCorrectly() throws Exception {
        LocalDateTime oneMonthAgo = now.minusMonths(1);
        Duration diff = Duration.between(oneMonthAgo, now);
        LocalDateTime prevStart = oneMonthAgo.minus(diff);
        LocalDateTime prevEnd = oneMonthAgo;

        when(repository.findDocumentStatisticsByCreatedOnBetween(
                eq(oneMonthAgo), eq(now))).thenReturn(currPeriod);
        when(repository.findDocumentStatisticsByCreatedOnBetween(eq(prevStart), eq(prevEnd))).thenReturn(prevPeriod);

        List<Double> deltas = documentReportService.getReportData(now.minusMonths(1), now)
                .stream()
                .map(DocumentReportItemDto::getViewsDelta)
                .toList();
        assertTrue(deltas.get(0) > 0);
        assertTrue(deltas.get(1) < 0);
        assertTrue(deltas.get(2) == 0);
        assertTrue(deltas.get(3) == -100);
        assertTrue(deltas.get(4) == 0);
        assertTrue(deltas.get(5) == 100);
    }

    @Test
    public void downloadsDeltaComputedCorrectly() throws Exception {
        LocalDateTime oneMonthAgo = now.minusMonths(1);
        Duration diff = Duration.between(oneMonthAgo, now);
        LocalDateTime prevStart = oneMonthAgo.minus(diff);
        LocalDateTime prevEnd = oneMonthAgo;

        when(repository.findDocumentStatisticsByCreatedOnBetween(
                eq(oneMonthAgo), eq(now))).thenReturn(currPeriod);
        when(repository.findDocumentStatisticsByCreatedOnBetween(eq(prevStart), eq(prevEnd))).thenReturn(prevPeriod);

        List<Double> deltas = documentReportService.getReportData(now.minusMonths(1), now)
                .stream()
                .map(DocumentReportItemDto::getDownloadsDelta)
                .toList();
        assertTrue(deltas.get(0) > 0);
        assertTrue(deltas.get(1) < 0);
        assertTrue(deltas.get(2) == 0);
        assertTrue(deltas.get(3) == -100);
        assertTrue(deltas.get(4) == 0);
        assertTrue(deltas.get(5) == 100);
    }
}