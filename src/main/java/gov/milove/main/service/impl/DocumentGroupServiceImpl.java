package gov.milove.main.service.impl;

import gov.milove.main.domain.DocumentGroup;
import gov.milove.main.dto.response.DocumentGroupDto;
import gov.milove.main.repository.jpa.DocumentGroupRepository;
import gov.milove.main.service.DocumentGroupService;
import gov.milove.main.service.DocumentService;
import gov.milove.main.util.mapper.DocumentGroupMapper;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class DocumentGroupServiceImpl implements DocumentGroupService {

    private final DocumentGroupRepository documentGroupRepository;

    private final DocumentService documentService;

    private final DocumentGroupMapper documentGroupMapper;

    @Override
    public void deleteById(Long id) {
       log.info("DELETE DOCUMENT GROUP - {}", id);

        DocumentGroup documentGroup = documentGroupRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        deleteGroup(documentGroup);
    }

    @Override
    public List<DocumentGroupDto> findAll() {
        return documentGroupRepository.findDistinctByDocumentGroup(null, Sort.by("order"))
            .stream().map(documentGroupMapper::toDocumentGroupDto)
            .toList();
    }

    public void deleteGroup(DocumentGroup documentGroup) {
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
}
