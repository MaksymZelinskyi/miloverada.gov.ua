package gov.milove.main.controller.integrationtest;

import gov.milove.config.IntegrationTest;
import gov.milove.main.domain.Action;
import gov.milove.main.domain.DocumentRetrieval;
import gov.milove.main.domain.MongoDocument;
import gov.milove.main.repository.jpa.DocumentStatisticsRepository;
import gov.milove.main.repository.mongo.MongoDocumentRepo;
import gov.milove.main.service.impl.DocumentReportService;
import lombok.extern.log4j.Log4j2;
import org.bson.types.Binary;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static gov.milove.testdata.DocumentTestData.DOCUMENT_NAME;
import static gov.milove.testdata.DocumentTestData.TEST_DATA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Log4j2
@Sql({"classpath:/testdata/document-test-data.sql"})
@DisplayName("Document stats web test")
public class DocumentStatsWebTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DocumentReportService documentReportService;
    @Autowired
    private DocumentStatisticsRepository documentStatisticsRepository;
    @Autowired
    private MongoDocumentRepo mongoDocumentRepo;

    @Test
    @DisplayName("Test document download is marked in the database")
    public void testDocumentDownloadRecorded() throws Exception {
        documentStatisticsRepository.deleteAll();
        mongoDocumentRepo.save(new MongoDocument(DOCUMENT_NAME, new Binary(TEST_DATA), MediaType.TEXT_PLAIN_VALUE));
        mockMvc.perform(get("/api/download/file/" + DOCUMENT_NAME))
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsByteArray()).isNotEmpty()
                );

        List<DocumentRetrieval> documentRetrievals = documentStatisticsRepository.findAll();
        assertEquals(1, documentRetrievals.size());
        assertNotNull(documentRetrievals.get(0).getDocument());
        assertEquals(DOCUMENT_NAME, documentRetrievals.get(0).getDocument().getName());
        assertEquals(Action.DOWNLOAD, documentRetrievals.get(0).getAction());
    }

    @Test
    @DisplayName("Test document report endpoint triggers document report service")
    public void testDocumentStatsReturned() throws Exception {
        LocalDateTime start = LocalDateTime.now().minusMonths(1);
        LocalDateTime end = LocalDateTime.now();
        when(documentReportService.getReport(any(), any())).thenReturn(TEST_DATA);

        mockMvc.perform(get("/api/reports/documents")
                        .param("start", start.toString())
                        .param("end", end.toString()))
                .andExpect(status().isOk())
                .andExpect(content().bytes(TEST_DATA));
    }
}

