package gov.milove.main.service.impl;

import gov.milove.main.domain.DocumentRetrieval;
import gov.milove.main.dto.DocumentReportItemDto;
import gov.milove.main.repository.jpa.DocumentStatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service responsible for managing and analyzing document retrieval statistics.
 * <p>
 * Provides functionality to persist document statistics and generate comparative
 * report data across different time periods. The service computes growth or decline
 * (delta) percentages for both views and downloads of documents.
 * </p>
 *
 * <p>
 * Example usage:
 * <pre>{@code
 * List<DocumentReportItemDto> report = documentStatsService.getReportData(
 *         LocalDateTime.now().minusMonths(1),
 *         LocalDateTime.now()
 * );
 * }</pre>
 * </p>
 *
 * @see DocumentStatisticsRepository
 * @see DocumentReportItemDto
 */
@RequiredArgsConstructor
@Service
public class DocumentStatsService {

    private final DocumentStatisticsRepository documentStatisticsRepository;

    /**
     * Persists a {@link DocumentRetrieval} instance into the statistics repository.
     *
     * @param documentRetrieval the document retrieval event to save
     */
    public void save(DocumentRetrieval documentRetrieval) {
        documentStatisticsRepository.save(documentRetrieval);
    }

    /**
     * Generates a list of {@link DocumentReportItemDto} objects for the specified time range.
     * <p>
     * Each item includes statistics about downloads and views, as well as the percentage
     * delta compared to the previous equivalent period.
     * </p>
     *
     * @param start the start date-time of the current reporting period (inclusive)
     * @param end   the end date-time of the current reporting period (exclusive)
     * @return a list of report items with calculated deltas
     */
    public List<DocumentReportItemDto> getReportData(LocalDateTime start, LocalDateTime end) {
        List<DocumentReportItemDto> currentPeriod = documentStatisticsRepository.findDocumentStatisticsByCreatedOnBetween(start, end);
        List<DocumentReportItemDto> previousPeriod = documentStatisticsRepository.findDocumentStatisticsByCreatedOnBetween(start.minus(Duration.between(start, end)), start);

        final Map<Long, DocumentReportItemDto> map = previousPeriod.stream().collect(Collectors.toMap(DocumentReportItemDto::getId, x -> x));
        currentPeriod.forEach(x -> {
            DocumentReportItemDto prev = map.getOrDefault(x.getId(), new DocumentReportItemDto());

            Long prevDownloads = prev.getDownloads();
            Long prevViews = prev.getViews();

            Long downloads = x.getDownloads();
            Long views = x.getViews();

            Double downloadsDelta = computeDelta(prevDownloads, downloads);
            Double viewsDelta = computeDelta(prevViews, views);

            x.setDownloadsDelta(downloadsDelta);
            x.setViewsDelta(viewsDelta);
        });
        return currentPeriod;
    }

    /**
     * Computes the percentage change (delta) between two numeric values.
     * <p>
     * The delta is calculated as:
     * <pre>
     * (current - previous) * 100 / previous
     * </pre>
     * with special handling for cases where values are {@code null} or zero.
     * </p>
     *
     * @param prev the value from the previous period (may be {@code null})
     * @param curr the value from the current period (may be {@code null})
     * @return the percentage change as a {@code double}, where
     *         <ul>
     *             <li>{@code 0.0} indicates no change</li>
     *             <li>{@code 100.0} indicates growth from zero to a positive value</li>
     *             <li>{@code -100.0} indicates decline to zero from a positive value</li>
     *         </ul>
     */
    public double computeDelta(Long prev, Long curr) {
        if (curr == null || curr == 0) {
            return (prev == null || prev == 0) ? 0d : -100d;
        }
        if(prev == null || prev==0) {
            return 100d;
        }
        return (curr - prev) * 100d / prev;
    }
}
