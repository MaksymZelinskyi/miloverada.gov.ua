package gov.milove.main.service.integrationtest;

import gov.milove.main.domain.Document;
import gov.milove.main.domain.DocumentGroup;
import gov.milove.main.domain.MongoDocument;
import gov.milove.main.repository.jpa.AppUserRepository;
import gov.milove.main.repository.jpa.DocumentGroupRepository;
import gov.milove.main.repository.jpa.DocumentRepository;
import gov.milove.main.repository.mongo.MongoDocumentRepo;
import gov.milove.main.service.impl.DocumentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class DocumentIntegrationTest extends AuthenticatedIntegrationTest {

    @Autowired
    private DocumentServiceImpl documentService;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private MongoDocumentRepo mongoDocumentRepo;
    @Autowired
    private DocumentGroupRepository groupRepository;
    @Autowired
    private AppUserRepository userRepository;
    @MockBean
    private JwtDecoder jwtDecoder;

    private DocumentGroup group;

    @BeforeEach
    void setUp() {
        group = new DocumentGroup();
        group.setName("Reports");
        group = groupRepository.save(group);
    }

    @Test
    void savesDocument() throws Exception {

        MockMultipartFile file = new MockMultipartFile("file", "report.txt", "text/plain", "Integration Test Data".getBytes());


        Document saved = documentService.saveDocument(group.getId(), file, "Monthly Report", user.getId());

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Monthly Report");
        assertThat(saved.getDocumentGroup().getId()).isEqualTo(group.getId());
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

        documentService.saveDocument(group.getId(), file, "doc", user.getId());
        documentService.saveDocument(group.getId(), file, "doc", user.getId());

        List<Document> docs = documentRepository.findAll();
        assertThat(docs).hasSize(1);
    }
}

