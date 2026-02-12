package gov.milove.main.service.impl;

import gov.milove.main.domain.News;
import gov.milove.main.domain.NewsComment;
import gov.milove.main.domain.NewsType;
import gov.milove.main.dto.NewsDtoWithImageAndType;
import gov.milove.main.dto.SimilarNewsDtoResponse;
import gov.milove.main.exception.NewsNotFoundException;
import gov.milove.main.exception.NewsServiceException;
import gov.milove.main.repository.jpa.NewsCommentRepository;
import gov.milove.main.repository.jpa.NewsImageRepository;
import gov.milove.main.repository.jpa.NewsRepository;
import gov.milove.main.repository.jpa.NewsTypeRepository;
import gov.milove.main.service.NewsImagesService;
import gov.milove.main.service.NewsService;
import gov.milove.main.util.mapper.NewsMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;


@Service
@Log4j2
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;

    private final NewsTypeRepository newsTypeRepository;

    private final NewsImagesService imageService;

    private final NewsImageRepository newsImageRepository;

    private final NewsCommentRepository newsCommentRepository;

    private final NewsMapper newsMapper;

    @Override
    public News save(News news, MultipartFile[] images, LocalDateTime dateOfPostponedPublication) {
        news.setImages(imageService.saveAll(List.of(images)));

        News saved = newsRepository.save(news);
        log.info("saved news = {}", saved);
        return saved;
    }

    @Override
    public void deleteById(Long id) {
        log.info("Delete news by id: {}", id);
        News news = newsRepository.findById(id).orElseThrow(NewsNotFoundException::new);
        imageService.deleteAllIfNotUsed(news.getImages());

        List<NewsComment> comments = newsCommentRepository.findAllByNewsIdOrderByCreatedOnDesc(id);
        log.info("Delete news comments: {}", comments.size());
        newsCommentRepository.deleteAll(comments);

        newsRepository.delete(news);
    }

    @Override
    public void update(NewsDtoWithImageAndType news) {
        News saved = newsRepository.findById(news.getId()).orElseThrow(NewsServiceException::new);
        news.mapToEntity(saved);
        saved.setLast_updated(LocalDateTime.now());
        defineNewsType(news, saved);
        newsRepository.save(saved);
    }

    @Override
    public void deleteNewsImageById(String mongoId) {
        imageService.deleteFromMongoIfNotUsed(mongoId);
        newsImageRepository.deleteByMongoImageId(mongoId);
    }

    @Override
    public List<SimilarNewsDtoResponse> findSimilarNewsByNewsType(Long newsId) {
        News news = newsRepository.findById(newsId).orElseThrow(NewsNotFoundException::new);
        log.info("News with id {} found, type: {}", newsId, news.getNewsType());

        if (news.getNewsType() == null) {
            log.info("News with id {} has no type, returning empty list", newsId);
            return List.of();
        }
        PageRequest pageRequest = PageRequest.of(0, 3)
                .withSort(Sort.Direction.DESC, "dateOfPublication");

        Page<News> similarNews = newsRepository.findAllByNewsType(news.getNewsType(), pageRequest);
        log.info("Found {} similar news for news with id {}", similarNews.getTotalElements(), newsId);

        return similarNews.stream()
                .map(newsMapper::toSimilarNewsDtoResponse)
                .filter((similarNewsDtoResponse) -> !similarNewsDtoResponse.id().equals(newsId))
                .toList();
    }

    private void defineNewsType(NewsDtoWithImageAndType news, News entity) {
        if (news.getNews_type_id().isEmpty()) {
            if (news.getTypeTitle() != null && news.getTitleExplanation() != null){
                entity.setNewsType(new NewsType(news.getTypeTitle(), news.getTitleExplanation()));
            }
        } else {
            long newsTypeId = Long.parseLong(news.getNews_type_id());
            if (newsTypeId == 0) entity.setNewsType(null);
            else {
                NewsType type = newsTypeRepository.findById(newsTypeId).orElseThrow(EntityNotFoundException::new);
                log.info(type);
                entity.setNewsType(type);
            }
        }
    }

}
