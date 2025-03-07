package gov.milove.main.util;

import gov.milove.main.exception.UtilException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Liashenko Andrii
 * @since 3/7/2025
 */
@Log4j2
public class IOUtil {

  public static List<ByteArrayResource> convertToInputStreamSource(
      MultipartFile[] multipartFiles) {
    if (multipartFiles == null) {
      return List.of();
    }
    return Stream.of(multipartFiles)
        .map(IOUtil::convertToInputStreamSource)
        .toList();
  }

  private static ByteArrayResource convertToInputStreamSource(MultipartFile file) {
    try {
      return new ByteArrayResource(file.getBytes()) {
        @Override
        public String getFilename() {
          return file.getOriginalFilename();
        }
      };
    } catch (IOException e) {
      log.error("Error getting input steam", e);
      throw new UtilException("Error getting input steam");
    }
  }
}
