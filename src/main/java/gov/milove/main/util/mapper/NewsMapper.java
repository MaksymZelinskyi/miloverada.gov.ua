package gov.milove.main.util.mapper;

import gov.milove.main.domain.News;
import gov.milove.main.dto.SimilarNewsDtoResponse;
import org.mapstruct.Mapper;

/**
 * @author Andrii_Liashenko
 */
@Mapper(
        config = MapperConfig.class
)
public interface NewsMapper {

    SimilarNewsDtoResponse toSimilarNewsDtoResponse(News news);
}
