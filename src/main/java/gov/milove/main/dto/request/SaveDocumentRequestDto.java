package gov.milove.main.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record SaveDocumentRequestDto(Long groupId, MultipartFile file, String title) {
}
