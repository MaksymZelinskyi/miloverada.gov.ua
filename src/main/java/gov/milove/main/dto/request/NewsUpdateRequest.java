package gov.milove.main.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

public record NewsUpdateRequest(@PathVariable Long id,
                                @RequestParam @NotBlank @Size(max = 300) String title,
                                @RequestParam @NotBlank String text,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateOfPublication) {
}
