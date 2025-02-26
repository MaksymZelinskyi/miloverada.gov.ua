package gov.milove.main.util.mapper;

import gov.milove.main.domain.LinkBanner;
import gov.milove.main.dto.LinkBannerDto;
import gov.milove.main.dto.request.LinkBannerCreateRequest;
import gov.milove.main.dto.request.LinkBannerUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Value;

@Mapper(
    config = MapperConfig.class,
    uses = AppUserMapper.class
)
public abstract class LinkBannerMapper {

  @Value("${app.image.base-url}")
  private String imageBaseUrl;

  @Mapping(target = "imageUrl", expression = "java(mapImageUrl(linkBanner))")
  public abstract LinkBannerDto toLinkBannerDto(LinkBanner linkBanner);

  @Mapping(target = "imageUrl", ignore = true)
  public abstract LinkBanner toLinkBanner(LinkBannerCreateRequest request);

  public abstract void updateLinkBannerFromDto(LinkBannerUpdateRequest request, @MappingTarget LinkBanner linkBanner);

  String mapImageUrl(LinkBanner linkBanner) {
    if (linkBanner.getImageUrl() != null) {
      return linkBanner.getImageUrl();
    }
    if (linkBanner.getImageId() != null) {
      return imageBaseUrl + linkBanner.getImageId();
    }
    return null;
  }
}
