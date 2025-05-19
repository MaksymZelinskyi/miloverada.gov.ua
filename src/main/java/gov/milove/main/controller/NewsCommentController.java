package gov.milove.main.controller;

import gov.milove.main.domain.NewsComment;
import gov.milove.main.dto.NewCommentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "News comment controller")
@RequestMapping("/api")
public interface NewsCommentController {

    @Operation(summary = "Get the news comment list")
    List<NewsComment> getComments(@PathVariable Long newsId);

    @Operation(summary = "Create a new comment")
    NewsComment newComment(@RequestBody NewCommentDto dto);

    @Operation(summary = "Delete a comment")
    Long delete(@PathVariable Long id);
}
