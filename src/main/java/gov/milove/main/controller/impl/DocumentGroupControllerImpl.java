package gov.milove.main.controller.impl;

import gov.milove.main.controller.DocumentGroupController;
import gov.milove.main.domain.Document;
import gov.milove.main.domain.DocumentGroup;
import gov.milove.main.dto.DocumentGroupWithGroupsDto;
import gov.milove.main.dto.DocumentGroupWithGroupsDtoAndDocumentsDto;
import gov.milove.main.dto.request.SaveDocumentRequestDto;
import gov.milove.main.exception.DocumentGroupNotFoundException;
import gov.milove.main.repository.jpa.DocumentGroupRepository;
import gov.milove.main.service.DocumentGroupService;
import gov.milove.main.service.DocumentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
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
    public DocumentGroupWithGroupsDtoAndDocumentsDto createNewSubGroup(Long groupId, String name) {
        return documentGroupService.saveSubGroup(groupId, name);
    }

    @Override
    @PutMapping("/protected/documentGroup/{id}/update")
    public Long editSubGroup(Long id, String name) {
        return documentGroupService.editSubGroup(id, name);
    }

    @Override
    @DeleteMapping("/protected/documentGroup/{id}/delete")
    public Long deleteSubGroup(Long id) {
        log.info("delete = {}", id);
        documentGroupService.deleteById(id);
        return id;
    }

    @Override
    @PostMapping("/protected/documentGroup/{id}/document/new")
    public Document newDoc(Long id, MultipartFile file, String title) {
        log.info("new doc = {}, size - {}, title = {}", file.getOriginalFilename(), file.getSize(), title);
        return documentService.saveDocument(new SaveDocumentRequestDto(id, file, title));
    }

    @Override
    @GetMapping("/documentGroup/id/{id}")
    public DocumentGroupWithGroupsDtoAndDocumentsDto findById(Long id) {
        return documentGroupRepository.findDistinctById(id).orElseThrow(DocumentGroupNotFoundException::new);
    }
}
