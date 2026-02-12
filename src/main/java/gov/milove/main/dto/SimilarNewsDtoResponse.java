package gov.milove.main.dto;

import gov.milove.main.domain.NewsImage;
import gov.milove.main.domain.NewsType;

import java.util.List;

/**
 * @author Andrii_Liashenko
 */
public record SimilarNewsDtoResponse(
        Long id,
        String description,
        String dateOfPublication,
        Long views,
        NewsType newsType,
        String image_id,
        Long commentsAmount,
        List<NewsImage> images
) {
}
