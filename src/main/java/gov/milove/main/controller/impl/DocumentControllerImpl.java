package gov.milove.main.controller.impl;

import gov.milove.main.controller.DocumentController;
import gov.milove.main.domain.Document;
import gov.milove.main.dto.DocumentWithGroupDto;
import gov.milove.main.repository.jpa.DocumentRepository;
import gov.milove.main.service.DocumentService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
public class DocumentControllerImpl implements DocumentController {

    private final DocumentRepository documentRepository;
    private final DocumentService documentService;

    @Override
    @PutMapping("/protected/document/{id}/update")
    public Long updateDocumentName(@PathVariable Long id, @RequestParam String name) {
        log.info("update doc = {}, name - {}", id, name);
        Document document = documentRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        document.setTitle(name);
        documentRepository.save(document);
        return id;
    }

    @Override
    @DeleteMapping("/protected/document/{id}/delete")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("/documents/search")
    public List<DocumentWithGroupDto> searchDocs(@RequestParam(name = "docName")  String encodedString)  {
        return documentRepository.searchDistinctByNameContainingIgnoreCaseOrTitleContainingIgnoreCase(encodedString, encodedString);
    }
}