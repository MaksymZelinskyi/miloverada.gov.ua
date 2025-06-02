package gov.milove.main.controller.impl;

import gov.milove.main.controller.DocumentGroupController;
import gov.milove.main.domain.Document;
import gov.milove.main.dto.DocumentGroupWithGroupsDto;
import gov.milove.main.dto.DocumentGroupWithGroupsDtoAndDocumentsDto;
import gov.milove.main.dto.request.SaveDocumentRequestDto;
import gov.milove.main.exception.DocumentGroupNotFoundException;
import gov.milove.main.repository.jpa.DocumentGroupRepository;
import gov.milove.main.service.DocumentGroupService;
import gov.milove.main.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api")
@Log4j2
public class DocumentGroupControllerImpl implements DocumentGroupController {

    private final DocumentGroupRepository documentGroupRepository;
    private final DocumentGroupService documentGroupService;
    private final DocumentService documentService;

    @Override
    @GetMapping("/documentGroup/all")
    public List<DocumentGroupWithGroupsDto> findAll() {
        return documentGroupRepository.findDistinctByDocumentGroupIdOrderByCreatedOn(null);
    }

    @Override
    @PostMapping("/protected/documentGroup/new")
    public DocumentGroupWithGroupsDtoAndDocumentsDto createNewSubGroup(
            @RequestParam(required = false) Long groupId,
            @RequestParam String name
    ) {
        return documentGroupService.saveSubGroup(groupId, name);
    }

    @Override
    @PutMapping("/protected/documentGroup/{id}/update")
    public Long editSubGroup(
            @PathVariable Long id,
            @RequestParam String name
    ) {
        return documentGroupService.editSubGroup(id, name);
    }

    @Override
    @DeleteMapping("/protected/documentGroup/{id}/delete")
    public ResponseEntity<Void> deleteSubGroup(@PathVariable Long id) {
        documentGroupService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/protected/documentGroup/{id}/document/new")
    public Document newDoc(
            @PathVariable Long id,
            @RequestParam MultipartFile file,
            @RequestParam String title
    ) {
        log.info("Add new document, filename: {}, size: {}, title: {}", file.getOriginalFilename(),
                file.getSize(), title);
        return documentService.saveDocument(new SaveDocumentRequestDto(id, file, title));
    }

    @Override
    @GetMapping("/documentGroup/id/{id}")
    public DocumentGroupWithGroupsDtoAndDocumentsDto findById(@PathVariable Long id) {
        return documentGroupRepository.findDistinctById(id)
                .orElseThrow(DocumentGroupNotFoundException::new);
    }
}
