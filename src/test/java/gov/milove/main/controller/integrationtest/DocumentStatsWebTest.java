package gov.milove.main.controller.integrationtest;

import gov.milove.config.IntegrationTest;
import gov.milove.main.service.impl.DocumentReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class DocumentStatsWebTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentReportService documentReportService;
    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    @WithMockUser(username = "mockuser@email.com")
    public void testDocumentStatsGenerated() throws Exception {
        byte[] bytes = "document report".getBytes();
        when(documentReportService.getReport(any(), any())).thenReturn(bytes);

        mockMvc.perform(get("/api/reports/documents").param("start", "2024-01-01T00:00:00").param("end", "2024-12-31T23:59:59")).andExpect(status().isOk()).andExpect(result -> {
            assertThat(result.getResponse().getContentAsByteArray()).isEqualTo(bytes);
        });
    }
}

