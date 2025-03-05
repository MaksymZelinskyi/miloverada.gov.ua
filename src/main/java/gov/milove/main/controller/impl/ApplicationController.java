package gov.milove.main.controller.impl;

import gov.milove.main.dto.request.ApplicationCreateRequest;
import gov.milove.main.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Liashenko Andrii
 * @since 3/5/2025
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApplicationController {

  private final ApplicationService applicationService;

  @PostMapping("/application")
  public ResponseEntity<Void> addApplication(@ModelAttribute ApplicationCreateRequest request) {
      applicationService.createApplication(request);
      return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }

}
