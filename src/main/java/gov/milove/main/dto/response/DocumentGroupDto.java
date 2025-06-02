package gov.milove.main.dto.response;


import java.util.Date;
import java.util.List;

/**
 * @author Liashenko Andrii
 * @since 4/7/2025
 */
public record DocumentGroupDto (
    String name,
    Long id,
    Date createdOn,
    List<DocumentGroupDto> groups,
    Long order
) {
}
