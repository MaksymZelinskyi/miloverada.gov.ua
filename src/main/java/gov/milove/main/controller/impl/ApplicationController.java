package gov.milove.main.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Liashenko Andrii
 * @since 3/5/2025
 */
@RestController
@RequiredArgsConstructor
public class ApplicationController {

  @PostMapping("/application")
  public void addApplication(@RequestParam("files") MultipartFile[] files) {

  }

}
