package gov.milove.main.service;

import gov.milove.main.dto.LinkBannerDto;
import gov.milove.main.dto.request.LinkBannerCreateRequest;
import gov.milove.main.dto.request.LinkBannerUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LinkBannerService {

  Page<LinkBannerDto> findAllBanners(Pageable pageable);

  LinkBannerDto save(LinkBannerCreateRequest request, String userId);

  LinkBannerDto update(LinkBannerUpdateRequest request);

  void deleteById(Long id);
}
