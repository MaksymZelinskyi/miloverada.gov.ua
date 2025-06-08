package gov.milove.main.service;

import jakarta.mail.MessagingException;

import java.time.LocalDateTime;

public interface ReportScheduler {

    void sendMonthlyReport();

    void sendReportBetween(LocalDateTime start, LocalDateTime end) throws MessagingException;
}
