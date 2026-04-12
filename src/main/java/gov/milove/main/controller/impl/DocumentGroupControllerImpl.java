package gov.milove.main.controller.impl;

import gov.milove.main.controller.DocumentGroupController;
import gov.milove.main.domain.Document;
import gov.milove.main.domain.DocumentGroup;
import gov.milove.main.dto.DocumentGroupWithGroupsDtoAndDocumentsDto;
import gov.milove.main.dto.response.DocumentGroupDto;
import gov.milove.main.exception.DocumentGroupNotFoundException;
import gov.milove.main.repository.jpa.DocumentGroupRepository;
import gov.milove.main.service.DocumentGroupService;
import gov.milove.main.service.DocumentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
@Validated
@RequestMapping("/api")
public class DocumentGroupControllerImpl implements DocumentGroupController {

    private final DocumentGroupRepository documentGroupRepository;
    private final DocumentGroupService documentGroupService;
    private final DocumentService documentService;

    @Override
    @GetMapping("/documentGroup/all")
    public List<DocumentGroupDto> findAll() {
        return documentGroupService.findAll();
    }

    @Override
    @PostMapping("/protected/documentGroup/new")
    public DocumentGroupWithGroupsDtoAndDocumentsDto createNewSubGroup(
            @RequestParam(required = false) Long groupId,
            @RequestParam String name) {

        DocumentGroup documentGroup = DocumentGroup.builder().documentGroup(groupId == null ? null : documentGroupRepository.getReferenceById(groupId)).name(name).build();
        DocumentGroup saved = documentGroupRepository.save(documentGroup);
        return documentGroupRepository.findDistinctById(saved.getId()).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    @PutMapping("/protected/documentGroup/{id}/update")
    public Long editSubGroup(@PathVariable Long id,
                             @RequestParam String name) {
        DocumentGroup group = documentGroupRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        group.setName(name);
        documentGroupRepository.save(group);
        return group.getId();
    }

    @Override
    @DeleteMapping("/protected/documentGroup/{id}/delete")
    public ResponseEntity<Void> deleteSubGroup(@PathVariable Long id) {
        documentGroupService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/protected/documentGroup/{id}/document/new")
    public Document newDoc(@PathVariable Long id,
                           @RequestParam MultipartFile file,
                           @RequestParam String title,
                           Principal principal) {
        log.info("Add new document, filename: {}, size: {}, title: {}", file.getOriginalFilename(),
                file.getSize(), title);
        return documentService.saveDocument(id, file, title, principal.getName());
    }

    @Override
    @GetMapping("/documentGroup/id/{id}")
    public DocumentGroupWithGroupsDtoAndDocumentsDto findById(@PathVariable Long id) {
        return documentGroupRepository.findDistinctById(id).orElseThrow(DocumentGroupNotFoundException::new);
    }

}
