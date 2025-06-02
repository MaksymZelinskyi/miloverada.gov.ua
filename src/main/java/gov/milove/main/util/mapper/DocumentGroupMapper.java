package gov.milove.main.util.mapper;

import gov.milove.main.domain.DocumentGroup;
import gov.milove.main.dto.response.DocumentGroupDto;
import org.mapstruct.Mapper;

/**
 * @author Liashenko Andrii
 * @since 4/7/2025
 */
@Mapper(
    config = MapperConfig.class
)
public interface DocumentGroupMapper {

    DocumentGroupDto toDocumentGroupDto(DocumentGroup documentGroup);

}
