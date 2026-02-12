package gov.milove.main.service.impl;

import gov.milove.main.dto.DocumentReportItemDto;
import gov.milove.main.exception.ReportGenerationException;
import gov.milove.main.service.ReportService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentReportService implements ReportService {

    private final DocumentStatsService documentStatsService;

    public byte[] getReport(LocalDateTime start, LocalDateTime end) {
        List<DocumentReportItemDto> data =  documentStatsService.getReportData(start, end);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Document Report");

        // Header Row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Title", "Doc group", "Downloads", "Downloads delta", "Views", "Views delta", "Added by"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Data Rows
        int rowIndex = 1;
        for (DocumentReportItemDto item : data) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(item.getId());
            row.createCell(1).setCellValue(item.getTitle());
            row.createCell(2).setCellValue(item.getDocumentGroup());
            row.createCell(3).setCellValue(item.getDownloads());
            row.createCell(4).setCellValue(item.getDownloadsDelta());
            row.createCell(5).setCellValue(item.getViews());
            row.createCell(6).setCellValue(item.getViewsDelta());
            row.createCell(7).setCellValue(item.getAddedBy());
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {

            workbook.write(baos);
            workbook.close();

            return baos.toByteArray();
        } catch (IOException e) {
            throw new ReportGenerationException("An error occurred while generation document report.");
        }
    }

}
