package gov.milove.main.service.impl;

import gov.milove.main.domain.News;
import gov.milove.main.domain.NewsImage;
import gov.milove.main.domain.NewsType;
import gov.milove.main.dto.NewsDtoWithImageAndType;
import gov.milove.main.dto.request.NewsCreateRequest;
import gov.milove.main.dto.request.NewsUpdateRequest;
import gov.milove.main.exception.NewsNotFoundException;
import gov.milove.main.exception.NewsServiceException;
import gov.milove.main.repository.jpa.NewsImageRepository;
import gov.milove.main.repository.jpa.NewsRepository;
import gov.milove.main.repository.jpa.NewsTypeRepository;
import gov.milove.main.service.NewsImagesService;
import gov.milove.main.service.NewsService;
import gov.milove.main.util.mapper.NewsMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
        News news = newsRepository.findById(id).orElseThrow(NewsNotFoundException::new);
        imageService.deleteAllIfNotUsed(news.getImages());
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

    public void updateNewsContent(NewsUpdateRequest req) {
        News news = newsRepository.findById(req.id()).orElseThrow(NewsNotFoundException::new);
        news.setDescription(req.title());
        news.setDateOfPublication(req.dateOfPublication());
        news.setMain_text(req.text());
        newsRepository.save(news);
    }

    @Override
    public void deleteNewsImageById(String mongoId) {
        imageService.deleteFromMongoIfNotUsed(mongoId);
        newsImageRepository.deleteByMongoImageId(mongoId);
    }

    private void defineNewsType(NewsDtoWithImageAndType news, News entity) {
        if (news.getNews_type_id() == null || news.getNews_type_id().isEmpty()) {
            if (news.getTypeTitle() != null && news.getTitleExplanation() != null) {
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

    public News createNews(NewsCreateRequest req) {
        NewsType newsType = req.newsTypeId() > 0 ? newsTypeRepository
                .findById(req.newsTypeId())
                .orElseThrow(EntityNotFoundException::new) : null;

        News news = newsMapper.toNews(req);
        return save(news, req.images(), req.dateOfPostponedPublication());
    }

    public List<NewsImage> addImagesToNews(Long newsId, MultipartFile[] files) {
        News news = newsRepository.findById(newsId).orElseThrow(NewsNotFoundException::new);
        List<NewsImage> newsImages = imageService.saveAll(List.of(files));
        news.getImages().addAll(newsImages);
        newsRepository.save(news);
        return newsImages;
    }

}
