package gov.milove.main.controller.integrationtest;

import gov.milove.main.domain.Document;
import gov.milove.main.domain.MongoDocument;
import gov.milove.main.repository.jpa.AppUserRepository;
import gov.milove.main.repository.jpa.DocumentRepository;
import gov.milove.main.repository.jpa.DocumentStatisticsRepository;
import gov.milove.main.repository.mongo.MongoDocumentRepo;
import org.bson.types.Binary;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.mockito.Mockito.when;

public class AbstractDocumentStatsIntegrationTest extends AuthenticatedIntegrationTest {

    protected final String TEST_FILENAME = "test filename";
    protected final String TEST_DATA = "test data";
    protected Long testDocumentId;
    protected Document testDoc;
    @Autowired
    protected DocumentStatisticsRepository documentStatisticsRepository;
    @MockBean
    protected MongoDocumentRepo mongoDocumentRepo;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private AppUserRepository appUserRepository;

    @BeforeEach
    public void setUp() {

        testDoc = new Document();
        testDoc.setName(TEST_FILENAME);
        testDoc.setTitle(TEST_FILENAME);
        testDoc.setHashCode(3);
        testDoc.setAddedBy(user);
        testDocumentId = documentRepository.save(testDoc).getId();
        MongoDocument mongoDocument = new MongoDocument(TEST_FILENAME, new Binary(TEST_DATA.getBytes()), "UTF-8");
        mongoDocumentRepo.save(mongoDocument);
        when(mongoDocumentRepo.findByFilename(TEST_FILENAME)).thenReturn(List.of(mongoDocument));
        documentRepository.flush();
    }
}
