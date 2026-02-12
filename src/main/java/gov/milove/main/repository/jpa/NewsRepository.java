package gov.milove.main.repository.jpa;

import gov.milove.main.domain.News;
import gov.milove.main.domain.NewsType;
import gov.milove.main.dto.INewsDto;
import gov.milove.main.dto.NewsDTO;
import gov.milove.main.dto.SimilarNewsDtoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface NewsRepository extends JpaRepository<News, Long> {

    Page<INewsDto> findDistinctBy(Pageable pageable);

    Page<News> findAllByNewsType(NewsType newsType, Pageable pageable);

    @Modifying
    @Transactional
    @Query("update News n set n.views = n.views + 1 where n.id = :newsId")
    void incrementViews(@Param("newsId") Long id);
}
