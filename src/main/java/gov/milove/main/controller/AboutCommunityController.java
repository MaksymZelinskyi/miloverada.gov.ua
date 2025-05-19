package gov.milove.main.controller;

import gov.milove.main.domain.About;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "About community controller")
@RestController
public interface AboutCommunityController {

    @Operation(summary = "Get the about community page")
    About getPage();

    @Operation(summary = "Update the about community page")
    String getUpdatePage(@RequestParam String mainText);
}
