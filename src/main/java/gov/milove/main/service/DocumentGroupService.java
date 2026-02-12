package gov.milove.main.service;

import gov.milove.main.dto.response.DocumentGroupDto;
import java.util.List;

public interface DocumentGroupService {

  void deleteById(Long id);

  List<DocumentGroupDto> findAll();

}
