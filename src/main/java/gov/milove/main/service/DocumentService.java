package gov.milove.main.service;

import gov.milove.main.domain.Document;
import gov.milove.main.dto.DocumentWithGroupDto;
import gov.milove.main.dto.request.SaveDocumentRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {

    Document saveDocument(SaveDocumentRequestDto request);

    void deleteAll(List<Document> documents);

    void delete(Document document);

    Document getDocument(Long id);

    Document saveDocument(Document document);

    List<DocumentWithGroupDto> searchDocument(String encodedString);

}
