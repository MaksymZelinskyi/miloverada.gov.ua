package gov.milove.main.repository.jpa;

import gov.milove.main.domain.DocumentRetrieval;
import gov.milove.main.dto.DocumentReportItemDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DocumentStatisticsRepository extends JpaRepository<DocumentRetrieval, Integer> {

    @Query("""
               select new gov.milove.main.dto.DocumentReportItemDto(
                                                                d.id,
                                                                d.title,
                                                                dg.name,
                                                                sum(case when dr.action = 'DOWNLOAD' then 1 else 0 end),
                                                                sum(case when dr.action = 'VIEW' then 1 else 0 end),
                                                                u.email
                                                            )
                                                            from Document d
                                                            join d.documentGroup dg
                                                            join d.addedBy u
                                                            left join DocumentRetrieval dr
                                                                on dr.createdOn between :start and :end
                                                            group by d.id, d.title, dg.name, u.email
            """)
    List<DocumentReportItemDto> findDocumentStatisticsByCreatedOnBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}