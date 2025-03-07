package gov.milove.main.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Liashenko Andrii
 * @since 3/5/2025
 */
public record ApplicationCreateRequest (
    @Pattern(regexp = "^\\S+\\s+\\S+\\s+\\S+") String fullName,
    @Size(max = 13) String phoneNumber,
    String email,
    @Size(max = 3000) String applicationText,

    @NotNull String tempNotificationDestination,
    MultipartFile[] files
) {

}
