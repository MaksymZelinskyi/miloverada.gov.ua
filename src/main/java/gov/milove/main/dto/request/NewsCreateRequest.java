package gov.milove.main.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public record NewsCreateRequest(@RequestParam @NotBlank @Size(max = 300) String title,
                                @RequestParam @NotBlank String text,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateOfPublication,
                                @RequestParam(required = false) @Future @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateOfPostponedPublication,
                                @RequestParam @NotEmpty @Size(max = 20) MultipartFile[] images,
                                @RequestParam Long newsTypeId) {
}
