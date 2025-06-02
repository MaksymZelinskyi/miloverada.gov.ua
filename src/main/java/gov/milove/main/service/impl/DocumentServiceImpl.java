package gov.milove.main.service.impl;

import gov.milove.main.domain.DocumentGroup;
import gov.milove.main.dto.DocumentGroupWithGroupsDtoAndDocumentsDto;
import gov.milove.main.repository.jpa.DocumentGroupRepository;
import gov.milove.main.service.DocumentGroupService;
import gov.milove.main.service.DocumentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class DocumentGroupServiceImpl implements DocumentGroupService {

    private final DocumentGroupRepository documentGroupRepository;
    private final DocumentService documentService;

    @Override
    public void deleteById(Long id) {
       log.info("DELETE DOCUMENT GROUP - {}", id);

        DocumentGroup documentGroup = documentGroupRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        deleteGroup(documentGroup);
    }

    private void deleteGroup(DocumentGroup documentGroup) {
        if (!documentGroup.getDocuments().isEmpty()) {
            documentService.deleteAll(documentGroup.getDocuments());
        }
        if (!documentGroup.getGroups().isEmpty()) {
            for (DocumentGroup group : documentGroup.getGroups()) {
                deleteGroup(group);
            }
        }

        documentGroupRepository.delete(documentGroup);
    }

    @Override
    public DocumentGroupWithGroupsDtoAndDocumentsDto saveSubGroup(Long groupId, String name) {
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

}
