package gov.milove.main.service;

import gov.milove.main.domain.News;
import gov.milove.main.dto.NewsDtoWithImageAndType;
import gov.milove.main.dto.SimilarNewsDtoResponse;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface NewsService {

    News save(News news, MultipartFile[] images, LocalDateTime dateOfPostponedPublication);

    void deleteById(Long id);

    void update(NewsDtoWithImageAndType news);

    void deleteNewsImageById(String mongoId);

    List<SimilarNewsDtoResponse> findSimilarNewsByNewsType(Long newsId);
}
