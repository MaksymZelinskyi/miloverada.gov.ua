package gov.milove.main.controller;

import gov.milove.main.domain.TextBanner;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Text banner controller")
@RequestMapping("/api/text-banner")
public interface TextBannerController {

    @Operation(summary = "Get all text banners")
    List<TextBanner> getAll();

    @Operation(summary = "Create text banner")
    ResponseEntity<Long> createBanner(@RequestBody TextBanner banner);

    @Operation(summary = "Update text banner")
    ResponseEntity update(@RequestBody TextBanner banner);

    @Operation(summary = "Delete text banner")
    ResponseEntity delete(@RequestParam("id") Long id);

}