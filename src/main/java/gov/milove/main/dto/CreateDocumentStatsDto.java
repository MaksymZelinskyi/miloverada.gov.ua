package gov.milove.main.dto;

import java.time.LocalDateTime;

public record CreateDocumentStatsDto(Long documentId, String documentName, LocalDateTime createdOn, String author) {

}
