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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
public class DocumentGroupControllerImpl implements DocumentGroupController {

    private final DocumentGroupRepository documentGroupRepository;
    private final DocumentGroupService documentGroupService;
    private final DocumentService documentService;


    @Override
    public List<DocumentGroupDto> findAll() {
        return documentGroupService.findAll();
    }

    @Override
    public DocumentGroupWithGroupsDtoAndDocumentsDto createNewSubGroup(Long groupId, String name) {

        DocumentGroup documentGroup = DocumentGroup.builder().documentGroup(groupId == null ? null : documentGroupRepository.getReferenceById(groupId)).name(name).build();
        DocumentGroup saved = documentGroupRepository.save(documentGroup);
        return documentGroupRepository.findDistinctById(saved.getId()).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public Long editSubGroup(Long id, String name) {
        DocumentGroup group = documentGroupRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        group.setName(name);
        documentGroupRepository.save(group);
        return group.getId();
    }

    @Override
    public Long deleteSubGroup(Long id) {
        log.info("delete = {}", id);
        documentGroupService.deleteById(id);
        return id;
    }

    @Override
    public Document newDoc(Long id, MultipartFile file, String title) {
        log.info("new doc = {}, size - {}, title = {}", file.getOriginalFilename(), file.getSize(), title);
        return documentService.saveDocument(id, file, title);
    }

    @Override
    public DocumentGroupWithGroupsDtoAndDocumentsDto findById(Long id) {
        return documentGroupRepository.findDistinctById(id).orElseThrow(DocumentGroupNotFoundException::new);
    }
}
