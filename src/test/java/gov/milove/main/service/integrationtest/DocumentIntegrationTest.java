package gov.milove.main.service.integrationtest;

import gov.milove.config.IntegrationTest;
import gov.milove.main.domain.Document;
import gov.milove.main.domain.MongoDocument;
import gov.milove.main.repository.jpa.DocumentRepository;
import gov.milove.main.repository.mongo.MongoDocumentRepo;
import gov.milove.main.service.impl.DocumentServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static gov.milove.testdata.AuhenticationTestData.TEST_USER_NAME;
import static gov.milove.testdata.DocumentTestData.GROUP_ID;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@Sql({"classpath:/testdata/appuser-test-data.sql", "classpath:/testdata/document-group-test-data.sql"})
class DocumentIntegrationTest extends IntegrationTest {

    @Autowired
    private DocumentServiceImpl documentService;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private MongoDocumentRepo mongoDocumentRepo;

    @Test
    void savesDocument() {

        MockMultipartFile file = new MockMultipartFile("file", "report.txt", "text/plain", "Integration Test Data".getBytes());


        Document saved = documentService.saveDocument(GROUP_ID, file, "Monthly Report", TEST_USER_NAME);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Monthly Report");
        assertThat(saved.getDocumentGroup().getId()).isEqualTo(GROUP_ID);
        assertThat(saved.getAddedBy()).isNotNull();

        Optional<MongoDocument> mongoDoc = mongoDocumentRepo.findById(saved.getMongoId());
        assertThat(mongoDoc).isPresent();
        assertThat(mongoDoc.get().getFilename()).isEqualTo("report.txt");

        List<Document> docs = documentRepository.findAll();
        assertThat(docs).isNotNull().isNotEmpty();
    }

    @Test
    void savesDocumentAndDetectsDuplicate() throws Exception {
        MultipartFile file = new MockMultipartFile("file", "doc.txt", "text/plain", "hello".getBytes());

        documentService.saveDocument(GROUP_ID, file, "doc", TEST_USER_NAME);
        documentService.saveDocument(GROUP_ID, file, "doc", TEST_USER_NAME);

        List<Document> docs = documentRepository.findAll();
        assertThat(docs).hasSize(1);
    }
}

