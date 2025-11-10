package gov.milove.main.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class DocumentRetrieval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Document document;
    @CreationTimestamp
    private LocalDateTime createdOn;
    @Enumerated(EnumType.STRING)
    private Action action;

    public DocumentRetrieval(Document document, Action action) {
        this.document = document;
        this.action = action;
    }

}