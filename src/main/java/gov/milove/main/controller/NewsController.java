package gov.milove.main.controller;

import gov.milove.main.domain.News;
import gov.milove.main.domain.NewsImage;
import gov.milove.main.domain.NewsType;
import gov.milove.main.dto.INewsDto;
import gov.milove.main.dto.NewsDtoWithImageAndType;
import gov.milove.main.dto.NewsPageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "News controller")
@Validated
public interface NewsController {

    @Operation(summary = "Get news page")
    List<INewsDto> newsAll(@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                           @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer size);

    @Operation(summary = "Add news type")
    NewsType saveNewsType(@Validated @RequestBody NewsType newsType);

    @Operation(summary = "Add news")
    ResponseEntity<Long> newNews(@RequestParam @NotBlank @Size(max = 300) String title,
                                 @RequestParam @NotBlank String text,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateOfPublication,
                                 @RequestParam(required = false) @Future @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateOfPostponedPublication,
                                 @RequestParam @NotEmpty @Size(max = 20) MultipartFile[] images,
                                 @RequestParam Long newsTypeId
    );

    @Operation(summary = "Delete news type")
    void deleteNewsTypeById(@PathVariable Long id);

    @Operation(summary = "Get news type")
    List<NewsType> getNewsTypes();

    @Operation(summary = "Delete news by id")
    ResponseEntity<Long> deleteNewsById(@PathVariable Long id);

    @Operation(summary = "Delete news image")
    ResponseEntity<String> deleteNewsImageById(@PathVariable String id);

    @Operation(summary = "Update news")
    ResponseEntity<Long> updateNews(@PathVariable Long id,
                                    @RequestParam @NotBlank @Size(max = 300) String title,
                                    @RequestParam @NotBlank String text,
                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateOfPublication);

    @Operation(summary = "Add news image")
    List<NewsImage> saveNewNewsImage(@PathVariable Long newsId,
                                     @RequestParam("images") MultipartFile[] files);

    @Operation(summary = "Get news by id")
    News getNewsById(@PathVariable("newsId") Long newsId);

    @Operation(summary = "Get similar news by id")
    List<INewsDto> getSimilarNewsByNewsId(@PathVariable Long newsId);

    @Operation(summary = "Get the most recent news")
    NewsPageDto getLatest(@RequestParam(defaultValue = "6", required = false) @Min(1) Integer pageSize,
                          @RequestParam(defaultValue = "0", required = false) Integer pageNumber);

    @Operation(summary = "Update news")
    ResponseEntity<String> updateNews(@ModelAttribute NewsDtoWithImageAndType news);

    @Operation(summary = "Increment views count")
    Long incrementViews(@PathVariable Long id);
}
