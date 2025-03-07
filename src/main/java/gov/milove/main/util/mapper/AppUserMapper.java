package gov.milove.main.util.mapper;

import gov.milove.main.domain.AppUser;
import gov.milove.main.dto.AppUserDto;
import org.mapstruct.Mapper;

/**
 * @author Liashenko Andrii
 * @since 2/24/2025
 */
@Mapper(
    config = MapperConfig.class
)
public interface AppUserMapper {

  AppUserDto toAppUserDto(AppUser appUser);
}
