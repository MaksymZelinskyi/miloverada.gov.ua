package gov.milove.main.controller.integrationtest;

import gov.milove.config.IntegrationTest;
import gov.milove.main.domain.Document;
import gov.milove.main.repository.jpa.DocumentRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static gov.milove.testdata.AuhenticationTestData.PROTECTED_API;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Log4j2
@Sql({"classpath:/testdata/appuser-test-data.sql", "classpath:/testdata/document-group-test-data.sql"})
@DisplayName("Document group controller integration test")
public class DocumentControllerIntegrationTest extends IntegrationTest {

    private static final int DOCUMENT_GROUP_ID = 1;
    private static final String DOCUMENT_TITLE = "Document Title";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DocumentRepository documentRepository;

    @Test
    @DisplayName("Should save document")
    void save_Doc_shouldSave_whenValidParams() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile(
                "file",
                "text.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Mock image content".getBytes()
        );

        MvcResult result = mockMvc.perform(multipart(String.format(PROTECTED_API + "/documentGroup/%d/document/new", DOCUMENT_GROUP_ID))
                        .file(imageFile)
                        .param("title", DOCUMENT_TITLE)
                        .with(jwt().jwt(jwt -> jwt.subject("user")).authorities(new SimpleGrantedAuthority("admin")))
                        .with(csrf()))
                .andDo(print())  // Print request/response for debugging
                .andExpectAll(
                        status().is2xxSuccessful(),
                        jsonPath("$.id").isNumber(),
                        jsonPath("$.createdOn").isNotEmpty(),
                        jsonPath("$.addedBy.id").value("user"),
                        jsonPath("$.title").value(DOCUMENT_TITLE)
                )
                .andReturn();

        Document document = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                Document.class
        );

        assertTrue(documentRepository.existsById(document.getId()));
    }
}
