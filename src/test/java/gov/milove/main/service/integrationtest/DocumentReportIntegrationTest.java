package gov.milove.main.service.integrationtest;

import gov.milove.config.IntegrationTest;
import gov.milove.main.controller.impl.DocumentControllerImpl;
import gov.milove.main.controller.impl.UploadImpl;
import gov.milove.main.domain.*;
import gov.milove.main.dto.DocumentReportItemDto;
import gov.milove.main.repository.jpa.AppUserRepository;
import gov.milove.main.repository.jpa.DocumentRepository;
import gov.milove.main.repository.jpa.DocumentStatisticsRepository;
import gov.milove.main.repository.mongo.MongoDocumentRepo;
import gov.milove.main.service.impl.DocumentStatsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.Binary;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static gov.milove.testdata.AuhenticationTestData.TEST_USER_NAME;
import static gov.milove.testdata.DocumentTestData.TEST_DATA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Sql({"classpath:/testdata/appuser-test-data.sql"})
public class DocumentReportIntegrationTest extends IntegrationTest {

    private static final int DOWNLOADS_COUNT = 5, VIEWS_COUNT = 10;
    private static final String TEST_FILENAME = "test filename";
    private static final String TEST_EMAIL = "user@email.com";

    @Autowired
    protected MongoDocumentRepo mongoDocumentRepo;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private DocumentStatisticsRepository documentStatisticsRepository;
    @Autowired
    private DocumentControllerImpl documentController;
    @Autowired
    private UploadImpl upload;
    @Autowired
    private DocumentStatsService documentStatsService;
    @Autowired
    private AppUserRepository appUserRepository;

    private Long testDocumentId;
    private Document testDoc;
    private LocalDateTime beforeSave;
    private LocalDateTime afterSave;

    @BeforeEach
    public void setUp() {
        AppUser user = appUserRepository.findById(TEST_USER_NAME).orElseThrow();
        testDoc = new Document();
        Optional<Document> found = documentRepository.findByName(TEST_FILENAME);
        if (found.isPresent())
            testDoc = found.get();

        testDoc.setName(TEST_FILENAME);
        testDoc.setTitle(TEST_FILENAME);
        testDoc.setHashCode(3);
        testDoc.setAddedBy(user);
        testDocumentId = documentRepository.save(testDoc).getId();
        MongoDocument mongoDocument = new MongoDocument(TEST_FILENAME, new Binary(TEST_DATA), "UTF-8");
        mongoDocumentRepo.save(mongoDocument);
    }

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
    @Order(1)
    public void testDocumentRetrievalsRecorded() {
        List<DocumentReportItemDto> list = documentStatisticsRepository.findDocumentStatisticsByCreatedOnBetween(beforeSave, afterSave);
        DocumentReportItemDto dto = list.stream().filter(x -> Objects.equals(x.getId(), testDocumentId)).findFirst().orElseThrow();

        assertEquals(DOWNLOADS_COUNT, dto.getDownloads());
        assertEquals(VIEWS_COUNT, dto.getViews());
    }

    @Test
    @Order(2)
    public void documentAddedToStatistics() {
        List<DocumentReportItemDto> stats = documentStatisticsRepository.findDocumentStatisticsByCreatedOnBetween(LocalDateTime.now().minusMonths(1), LocalDateTime.now());

        assertThat(stats).isNotNull().isNotEmpty();
        assertThat(stats.get(0).getTitle()).isEqualTo(TEST_FILENAME);
        assertThat(stats.get(0).getAddedBy()).isEqualTo(TEST_EMAIL);
    }

    @Test
    @Order(3)
    public void deltasWrittenProperly() {
        List<DocumentReportItemDto> stats = documentStatsService.getReportData(LocalDateTime.now().minusMonths(1), LocalDateTime.now());

        assertEquals(100.0, stats.get(0).getDownloadsDelta());
        assertEquals(100.0, stats.get(0).getViewsDelta());
    }

    @Test
    @Order(4)
    public void viewsAndDownloadsAreCounted() {
        documentController.markAsViewed(testDocumentId);
        upload.findDocumentByFilename(TEST_FILENAME, mock(HttpServletResponse.class));

        List<DocumentReportItemDto> stats = documentStatisticsRepository.findDocumentStatisticsByCreatedOnBetween(LocalDateTime.now().minusMonths(1), LocalDateTime.now());

        assertThat(stats).isNotNull().isNotEmpty();
        assertThat(stats.get(0).getViews()).isGreaterThan(0);
        assertThat(stats.get(0).getDownloads()).isGreaterThan(0);
    }
}
