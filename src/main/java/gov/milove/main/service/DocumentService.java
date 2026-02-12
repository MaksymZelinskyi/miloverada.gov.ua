package gov.milove.main.service;

import gov.milove.main.domain.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DocumentService {

    void deleteById(Long id);

    Document saveDocument(Long groupId, MultipartFile file, String name, String userId);

    void deleteAll(List<Document> documents);

    Document getById(Long id);

    Document getByName(String name);
}
