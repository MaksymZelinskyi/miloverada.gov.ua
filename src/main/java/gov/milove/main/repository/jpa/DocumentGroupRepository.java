package gov.milove.main.repository.jpa;

import gov.milove.main.domain.DocumentGroup;
import gov.milove.main.dto.DocumentGroupWithGroupsDtoAndDocumentsDto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentGroupRepository extends JpaRepository<DocumentGroup, Long> {

    Optional<DocumentGroupWithGroupsDtoAndDocumentsDto> findDistinctById(Long id);

    List<DocumentGroup> findDistinctByDocumentGroup(Long id, Sort sort);
}
