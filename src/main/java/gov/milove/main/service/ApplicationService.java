package gov.milove.main.service;

import gov.milove.main.dto.request.ApplicationCreateRequest;
import java.util.List;
import org.springframework.core.io.ByteArrayResource;

/**
 * @author Liashenko Andrii
 * @since 3/5/2025
 */
public interface ApplicationService {

  void createApplication(ApplicationCreateRequest request, List<ByteArrayResource> inputStreamResourceList);

}
