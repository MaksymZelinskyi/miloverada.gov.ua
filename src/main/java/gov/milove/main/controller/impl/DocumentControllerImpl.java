package gov.milove.main.controller.impl;

import gov.milove.main.controller.DocumentController;
import gov.milove.main.domain.Document;
import gov.milove.main.dto.DocumentWithGroupDto;
import gov.milove.main.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
public class DocumentControllerImpl implements DocumentController {

    private final DocumentService documentService;

    @Override
    public Long updateDocumentName(Long id, String name) {
        log.info("update doc = {}, name - {}", id, name);
        Document document = documentService.getDocument(id);
        document.setTitle(name);
        documentService.saveDocument(document);
        return id;
    }

    @Override
    public Document deleteDocument(Long id) {
        Document document = documentService.getDocument(id);
        documentService.delete(document);
        return document;
    }

    @Override
    public List<DocumentWithGroupDto> searchDocs(String encodedString) {
        return documentService.searchDocument(encodedString);
    }
}