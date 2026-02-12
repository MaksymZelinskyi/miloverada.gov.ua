package gov.milove.main.service;

import java.time.LocalDateTime;


public interface ReportService {

    byte[] getReport(LocalDateTime start, LocalDateTime end) ;

}
