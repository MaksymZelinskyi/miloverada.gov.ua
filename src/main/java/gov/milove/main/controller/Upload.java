package gov.milove.main.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Download")
public interface Upload {

  @Operation(summary = "Get image by id")
  ResponseEntity<byte[]> getImage(String id);

  @Operation(summary = "Get image by id")
  ResponseEntity<byte[]> getImageV2(String id, HttpServletResponse response);

  @Operation(summary = "Get document by filename")
  byte[] findDocumentByFilename(String fileName, HttpServletResponse response)
      throws UnsupportedEncodingException;
}
