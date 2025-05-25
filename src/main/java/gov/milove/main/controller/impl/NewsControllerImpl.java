package gov.milove.main.controller.impl;

import gov.milove.main.controller.NewsController;
import gov.milove.main.domain.News;
import gov.milove.main.domain.NewsImage;
import gov.milove.main.domain.NewsType;
import gov.milove.main.dto.INewsDto;
import gov.milove.main.dto.NewsDtoWithImageAndType;
import gov.milove.main.dto.NewsPageDto;
import gov.milove.main.dto.request.NewsCreateRequest;
import gov.milove.main.dto.request.NewsUpdateRequest;
import gov.milove.main.exception.IllegalParameterException;
import gov.milove.main.exception.NewsNotFoundException;
import gov.milove.main.repository.jpa.NewsRepository;
import gov.milove.main.repository.jpa.NewsTypeRepository;
import gov.milove.main.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@Log4j2
@RequiredArgsConstructor
public class NewsControllerImpl implements NewsController {

    private final NewsService newsService;
    private final NewsRepository newsRepository;
    private final NewsTypeRepository newsTypeRepository;

    @Override
    @GetMapping("/news/all")
    public List<INewsDto> newsAll(Integer page, Integer size) {
        return newsRepository
                .findDistinctBy(PageRequest.of(page, size)
                        .withSort(Sort.Direction.DESC, "dateOfPublication"))
                .toList();
    }

    @Override
    @PostMapping("/protected/newsType/new")
    public NewsType saveNewsType(NewsType newsType) {
        return newsTypeRepository.save(newsType);
    }

    @Override
    @Transactional
    @PostMapping("/protected/news/new")
    public ResponseEntity<Long> newNews(NewsCreateRequest req) {
        log.info("CREATE NEWS");
        log.info("images length = = {}", req.images().length);
        log.info("title = {}, dateOfPublication = {}, dateOfPostponedPublication = {}", req.title(), req.dateOfPublication(), req.dateOfPostponedPublication());
        log.info("newsType = {}", req.newsTypeId());

        return ResponseEntity.ok().body(newsService.createNews(req).getId());
    }

    @Override
    @DeleteMapping("/protected/newsType/{id}/delete")
    public void deleteNewsTypeById(Long id) {
        if (id <= 0) throw new IllegalParameterException("Id must be higher than zero");
        newsTypeRepository.deleteById(id);
    }


    @Override
    @GetMapping("/protected/news-types")
    public List<NewsType> getNewsTypes() {
        return newsTypeRepository.findAll();
    }


    @Override
    @DeleteMapping("/protected/news/{id}/delete")
    public ResponseEntity<Long> deleteNewsById(Long id) {
        if (id <= 0) throw new IllegalParameterException("Id must be higher than zero");
        newsService.deleteById(id);
        return ResponseEntity.accepted().body(id);
    }

    @DeleteMapping("/protected/news/image/{id}/delete")
    public ResponseEntity<String> deleteNewsImageById(String id) {
        if (!ObjectId.isValid(id)) throw new IllegalParameterException("Image id hex string is not valid");
        newsService.deleteNewsImageById(id);
        return ResponseEntity.accepted().body(id);
    }

    @Override
    @PutMapping("/protected/news/{id}/update")
    public ResponseEntity<Long> updateNews(NewsUpdateRequest req) {
        log.info("title = {}, text = {}, date = {}", req.title(), req.text(), req.dateOfPublication());
        newsService.updateNewsContent(req);

        return ResponseEntity.accepted().body(req.id());
    }

    @Override
    @PostMapping("/protected/news/{newsId}/image/new")
    public List<NewsImage> saveNewNewsImage(Long newsId, MultipartFile[] files) {
        return newsService.addImagesToNews(newsId, files);
    }

    @GetMapping("/news/{newsId}")
    public News getNewsById(Long newsId) {
        return newsRepository.findById(newsId).orElseThrow(NewsNotFoundException::new);
    }

    @Override
    @GetMapping("/news/{newsId}/similar")
    public List<INewsDto> getSimilarNewsByNewsId(Long newsId) {
        News news = newsRepository.findById(newsId).orElseThrow(NewsNotFoundException::new);

        return (news.getNewsType() != null ? newsRepository
                .getLastNewsDTOByNewsTypeIdWithLimit(newsId, news.getNewsType().getId(), PageRequest.of(0, 3)
                        .withSort(Sort.Direction.DESC, "dateOfPublication")).toList() : List.of());
    }

    @Override
    @GetMapping("/news/latest")
    public NewsPageDto getLatest(Integer pageSize, Integer pageNumber) {
        Page<INewsDto> newsDtos = newsRepository
                .findDistinctBy(PageRequest.ofSize(pageSize)
                        .withPage(pageNumber)
                        .withSort(Sort.Direction.DESC, "dateOfPublication"));
        return new NewsPageDto(newsDtos);
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/news/update")
    public ResponseEntity<String> updateNews(NewsDtoWithImageAndType news) {
        newsService.update(news);
        return new ResponseEntity<>("Оновлення успішне", HttpStatus.OK);
    }

    @Override
    @PostMapping("/news/{id}/incrementViews")
    public Long incrementViews(Long id) {
        newsRepository.incrementViews(id);
        return id;
    }
}
