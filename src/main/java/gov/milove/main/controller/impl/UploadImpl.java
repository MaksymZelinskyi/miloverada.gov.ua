package gov.milove.main.controller.impl;

import gov.milove.main.controller.Upload;
import gov.milove.main.domain.*;
import gov.milove.main.exception.ImageNotFoundException;
import gov.milove.main.repository.mongo.ImageRepo;
import gov.milove.main.repository.mongo.MongoDocumentRepo;
import gov.milove.main.repository.mongo.NewsImagesMongoRepo;
import gov.milove.main.service.DocumentService;
import gov.milove.main.service.impl.DocumentStatsService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.bson.types.Binary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api")
public class UploadImpl implements Upload {

  private final NewsImagesMongoRepo newsImagesMongoRepo;

  private final MongoDocumentRepo mongoDocumentRepo;
  private final ImageRepo imageRepo;
  private final DocumentStatsService documentStatsService;
  private final DocumentService documentService;

  @Override
  @GetMapping("/download/image/{id}")
  public ResponseEntity<byte[]> getImage(@PathVariable String id) {
    MongoNewsImage mongoNewsImage = newsImagesMongoRepo.findById(id)
        .orElseThrow(ImageNotFoundException::new);
    MediaType mediaType;
    if (mongoNewsImage.getContentType() == null) {
      mediaType = MediaType.IMAGE_PNG;
    } else {
      mediaType = MediaType.parseMediaType(mongoNewsImage.getContentType());
    }
    return ResponseEntity.ok().contentType(mediaType)
        .body(mongoNewsImage.getBinaryImage().getData());
  }

  @Override
  @GetMapping("/download/v2/image/{id}")
  public ResponseEntity<byte[]> getImageV2(@PathVariable String id, HttpServletResponse response) {
    Image image = imageRepo.findById(id).orElseThrow(ImageNotFoundException::new);
    MediaType mediaType = MediaType.parseMediaType(image.getContentType());
    String contentDisposition = "attachment; filename=\"" + image.getFilename() + "\"";
    response.addHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
    return ResponseEntity.ok().contentType(mediaType).contentLength(image.getBinaryImage().length())
        .body(image.getBinaryImage().getData());
  }

  @Override
  @GetMapping("/download/file/{fileName}")
  public byte[] findDocumentByFilename(@PathVariable String fileName, HttpServletResponse response) {
    List<MongoDocument> mongoDocuments = mongoDocumentRepo.findByFilename(fileName);
      if (mongoDocuments.isEmpty()) {
          throw new EntityNotFoundException("document with name " + fileName + ", not found");
      }
    MongoDocument mongoDocument = mongoDocuments.get(0);

    if (mongoDocuments.size() > 1) {
      for (MongoDocument document : mongoDocuments.subList(1, mongoDocuments.size())) {
        mongoDocumentRepo.deleteById(document.getId());
      }

    }
    Binary mongoFile = mongoDocument.getFile();

    Document document = documentService.getByName(fileName);
    documentStatsService.save(new DocumentRetrieval(document, Action.DOWNLOAD));

    String encodedFilename = URLEncoder.encode(mongoDocument.getFilename(), StandardCharsets.UTF_8);
    String contentDisposition = "attachment; filename=\"" + encodedFilename + "\"";
    response.addHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
    response.addHeader(HttpHeaders.CONTENT_TYPE, mongoDocument.getContentType());
    response.addHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(mongoFile.length()));

    return mongoFile.getData();
  }
}
