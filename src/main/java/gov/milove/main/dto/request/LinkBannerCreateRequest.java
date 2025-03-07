package gov.milove.main.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;
import org.springframework.web.multipart.MultipartFile;

public record LinkBannerCreateRequest (
    @NotBlank @URL
    String url,
    @NotBlank @Size(min = 3)
    String text,
    String imageUrl,
    MultipartFile imageFile
) {

}
