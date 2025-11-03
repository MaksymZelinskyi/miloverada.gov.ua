package gov.milove.main.controller.impl;

import gov.milove.main.controller.ReportController;
import gov.milove.main.dto.DocumentReportItemDto;
import gov.milove.main.service.ReportService;
import gov.milove.main.service.impl.DocumentStatsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class DocumentReportController implements ReportController {

    private final ReportService reportService;

    @GetMapping("/reports/documents")
    @Override
    public ResponseEntity<InputStreamResource> getReport(HttpServletResponse response, @RequestParam LocalDateTime start, @RequestParam LocalDateTime end) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        );
        headers.setContentDispositionFormData("attachment", "document-report.xlsx");
        headers.setCacheControl("no-cache, no-store, must-revalidate");
        InputStreamResource data = new InputStreamResource(new ByteArrayInputStream(reportService.getReport(start, end)));

        return ResponseEntity.ok()
                .headers(headers)
                .body(data);
    }

    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response, @RequestParam LocalDateTime start, @RequestParam LocalDateTime end) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=document-report.xlsx");

        reportService.getReport(start, end);
    }
}
