package gov.milove.main.controller;

import gov.milove.main.domain.News;
import gov.milove.main.domain.NewsImage;
import gov.milove.main.domain.NewsType;
import gov.milove.main.dto.INewsDto;
import gov.milove.main.dto.NewsDtoWithImageAndType;
import gov.milove.main.dto.NewsPageDto;
import gov.milove.main.dto.request.NewsCreateRequest;
import gov.milove.main.dto.request.NewsUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "News controller")
@Validated
@RequestMapping("/api")
public interface NewsController {

    @Operation(summary = "Get news page")
    @GetMapping("/news/all")
    List<INewsDto> newsAll(@RequestParam(value = "page", defaultValue = "0") Integer page,
                           @RequestParam(value = "pageSize", defaultValue = "10") Integer size);

    @Operation(summary = "Add news type")
    @PostMapping("/protected/newsType/new")
    NewsType saveNewsType(@Valid @RequestBody NewsType newsType);

    @Operation(summary = "Add news")
    @PostMapping("/protected/news/new")
    ResponseEntity<Long> newNews(@Valid @ModelAttribute NewsCreateRequest req);

    @Operation(summary = "Delete news type")
    @DeleteMapping("/protected/newsType/{id}/delete")
    void deleteNewsTypeById(@PathVariable @Positive Long id);

    @Operation(summary = "Get news type")
    @GetMapping("/protected/news-types")
    List<NewsType> getNewsTypes();

    @Operation(summary = "Delete news by id")
    @DeleteMapping("/protected/news/{id}/delete")
    ResponseEntity<Long> deleteNewsById(@PathVariable @Positive Long id);

    @Operation(summary = "Delete news image")
    @DeleteMapping("/protected/news/image/{id}/delete")
    ResponseEntity<String> deleteNewsImageById(@PathVariable String id);

    @Operation(summary = "Update news")
    @PutMapping("/protected/news/{id}/update")
    ResponseEntity<Long> updateNews(@Valid @ModelAttribute NewsUpdateRequest req);

    @Operation(summary = "Add news image")
    @PostMapping("/protected/news/{newsId}/image/new")
    List<NewsImage> saveNewNewsImage(@PathVariable @Positive Long newsId,
                                     @RequestParam("images") MultipartFile[] files);

    @Operation(summary = "Get news by id")
    @GetMapping("/news/{newsId}")
    News getNewsById(@PathVariable("newsId") @Positive Long newsId);

    @Operation(summary = "Get similar news by id")
    @GetMapping("/news/{newsId}/similar")
    List<INewsDto> getSimilarNewsByNewsId(@PathVariable("newsId") @Positive Long newsId);

    @Operation(summary = "Get the most recent news")
    @GetMapping("/news/latest")
    NewsPageDto getLatest(@RequestParam(defaultValue = "6") @Min(1) Integer pageSize,
                          @RequestParam(defaultValue = "0") Integer pageNumber);

    @Operation(summary = "Update news (admin only)")
    @PostMapping("/news/update")
    ResponseEntity<String> updateNews(@Valid @ModelAttribute NewsDtoWithImageAndType news);

    @Operation(summary = "Increment views count")
    @PostMapping("/news/{id}/incrementViews")
    Long incrementViews(@PathVariable("id") @Positive Long id);
}
