package gov.milove.main.service;

import gov.milove.main.domain.LinkBanner;
import gov.milove.main.dto.LinkBannerDto;
import gov.milove.main.dto.request.LinkBannerUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LinkBannerService {

  Page<LinkBannerDto> findAllBanners(Pageable pageable);

  LinkBanner save(LinkBanner linkBanner, String userId);

  LinkBanner update(LinkBannerUpdateRequest request);

  void deleteById(Long id);
}
