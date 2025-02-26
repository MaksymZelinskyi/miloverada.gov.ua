package gov.milove.main.controller.impl;

import gov.milove.main.service.ImageService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Liashenko Andrii
 * @since 2/26/2025
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Log4j2
public class ImageController {

  private final ImageService imageService;

  @GetMapping("/images/{id}")
  public ResponseEntity<InputStreamResource> getImage(@PathVariable String id) throws IOException {
    log.info("Get image with id {}", id);

    GridFsResource resource = imageService.getImage(id);

    if (resource == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(resource.getContentType()))
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + resource.getFilename() + "\"")
        .body(new InputStreamResource(resource.getInputStream()));
  }
}
