package gov.milove.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentReportItemDto {

    private Long id;
    private String title;
    private String documentGroup;
    private Long downloads;
    private Double downloadsDelta;
    private Long views;
    private Double viewsDelta;
    private String addedBy;

    public DocumentReportItemDto(Long id, String title, String documentGroup, Long downloads,
                              Long views, String addedBy) {
        this(id, title, documentGroup, downloads, null, views, null, addedBy);
    }

}
