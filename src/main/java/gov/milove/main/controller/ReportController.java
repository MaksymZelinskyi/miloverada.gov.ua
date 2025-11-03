package gov.milove.main.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.time.LocalDateTime;

@Tag(name = "Report controller")
public interface ReportController {

    @Operation(summary = "Get the report for the period specified",
            description = "Returns the report in xls format. Takes the period boundaries(inclusive)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Report generated successfully"),
            @ApiResponse(responseCode = "500", description = "An error occurred during report generation")
    })
    ResponseEntity<InputStreamResource> getReport(HttpServletResponse response, LocalDateTime start, LocalDateTime end);

}

