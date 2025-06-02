package gov.milove.main.util.mapper;

import gov.milove.main.domain.News;
import gov.milove.main.dto.request.NewsCreateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        config = MapperConfig.class
)
public interface NewsMapper {

    @Mapping(target = "description", source = "title")
    @Mapping(target = "main_text", source = "text")
    News toNews(NewsCreateRequest dto);

}
