package gov.milove.main.controller.impl;

import static org.springframework.http.HttpStatus.CREATED;

import gov.milove.main.controller.LinkBannerController;
import gov.milove.main.dto.LinkBannerDto;
import gov.milove.main.dto.request.LinkBannerCreateRequest;
import gov.milove.main.dto.request.LinkBannerUpdateRequest;
import gov.milove.main.service.LinkBannerService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Log4j2
public class LinkBannerControllerImpl implements LinkBannerController {

  private final LinkBannerService linkBannerService;

  @Override
  @GetMapping("/link-banners")
  public Page<LinkBannerDto> findAll(@PageableDefault Pageable pageable) {
    return linkBannerService.findAllBanners(pageable);
  }

  @Override
  @PostMapping("/protected/link-banners")
  public ResponseEntity<LinkBannerDto> addBanner(
      @ModelAttribute LinkBannerCreateRequest request, Principal user) {
    LinkBannerDto saved = linkBannerService.save(request, user.getName());

    return new ResponseEntity<>(saved, CREATED);
  }

  @Override
  @PutMapping("/protected/link-banners")
  public ResponseEntity<LinkBannerDto> update(@RequestBody LinkBannerUpdateRequest request) {
    LinkBannerDto linkBanner = linkBannerService.update(request);

    return new ResponseEntity<>(linkBanner, HttpStatus.OK);
  }

  @Override
  @DeleteMapping("/protected/link-banners/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    log.info("Delete link banner by id: {}", id);
    linkBannerService.deleteById(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}