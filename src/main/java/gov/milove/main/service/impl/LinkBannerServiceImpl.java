package gov.milove.main.service.impl;

import static gov.milove.main.constants.ExceptionConstants.APP_USER_NOT_FOUND_BY_ID;

import gov.milove.main.domain.AppUser;
import gov.milove.main.domain.LinkBanner;
import gov.milove.main.dto.LinkBannerDto;
import gov.milove.main.dto.request.LinkBannerCreateRequest;
import gov.milove.main.dto.request.LinkBannerUpdateRequest;
import gov.milove.main.exception.AppUserNotFoundException;
import gov.milove.main.exception.LinkBannerNotFoundException;
import gov.milove.main.exception.ValidationException;
import gov.milove.main.repository.jpa.AppUserRepository;
import gov.milove.main.repository.jpa.LinkBannerRepository;
import gov.milove.main.service.ImageService;
import gov.milove.main.service.LinkBannerService;
import gov.milove.main.util.mapper.LinkBannerMapper;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Log4j2
public class LinkBannerServiceImpl implements LinkBannerService {

  private final LinkBannerRepository linkBannerRepository;

  private final AppUserRepository appUserRepository;

  private final ImageService imageService;

  private final LinkBannerMapper linkBannerMapper;

  @Override
  public Page<LinkBannerDto> findAllBanners(Pageable pageable) {
    return linkBannerRepository.findAll(pageable)
        .map(linkBannerMapper::toLinkBannerDto);
  }

  @Override
  public LinkBannerDto save(LinkBannerCreateRequest request, String userId) {
    LinkBanner linkBanner = linkBannerMapper.toLinkBanner(request);
    log.info("Save link banner from user: {}", userId);

    validateImageParams(request);

    if (Objects.nonNull(request.imageFile())) {
      String imageId = imageService.saveImage(request.imageFile());
      linkBanner.setImageId(imageId);
    } else {
      linkBanner.setImageUrl(request.imageUrl());
    }

    AppUser user = appUserRepository.findById(userId)
        .orElseThrow(() -> new AppUserNotFoundException(APP_USER_NOT_FOUND_BY_ID.formatted(userId)));
    linkBanner.setAddedBy(user);
    LinkBanner saved =linkBannerRepository.save(linkBanner);
    return linkBannerMapper.toLinkBannerDto(saved);
  }

  private void validateImageParams(LinkBannerCreateRequest request) {
    if (Objects.isNull(request.imageFile()) && Objects.isNull(request.imageUrl())) {
      throw new ValidationException("Image url or file should be provided");
    }
  }

  @Override
  public LinkBannerDto update(LinkBannerUpdateRequest request) {
    LinkBanner linkBanner = findById(request.id());
    linkBannerMapper.updateLinkBannerFromDto(request, linkBanner);
    LinkBanner saved = linkBannerRepository.save(linkBanner);
    return linkBannerMapper.toLinkBannerDto(saved);
  }

  @Override
  public void deleteById(Long id) {
    findById(id);
    linkBannerRepository.deleteById(id);
  }

  private LinkBanner findById(Long id) {
    return linkBannerRepository.findById(id).orElseThrow(() ->
        new LinkBannerNotFoundException("Link banner with id: %s not found".formatted(id)));
  }
}
