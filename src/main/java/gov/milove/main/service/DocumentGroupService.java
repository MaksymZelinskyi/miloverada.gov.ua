package gov.milove.main.service;

import gov.milove.main.dto.DocumentGroupWithGroupsDtoAndDocumentsDto;

public interface DocumentGroupService {

  void deleteById(Long id);

  DocumentGroupWithGroupsDtoAndDocumentsDto saveSubGroup(Long groupId, String name);

  Long editSubGroup(Long id, String name);

}