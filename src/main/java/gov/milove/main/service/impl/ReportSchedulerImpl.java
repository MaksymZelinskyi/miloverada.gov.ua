package gov.milove.main.service.impl;

import gov.milove.main.exception.MailException;
import gov.milove.main.service.ReportScheduler;
import gov.milove.main.service.ReportService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * This service class implements methods defined in its interface,
 * providing scheduled behaviour that is executed automatically every month.
 * It retrieves the report on the document operations performed last month using {@code ReportService}
 * and sends it inside an email to the admin using {@code JavaMailSender}
 *
 * @see ReportService
 */
@Slf4j
@Service
public class ReportSchedulerImpl implements ReportScheduler {

    private final ReportService reportService;
    private final String sender;
    private final String admin;
    private final JavaMailSender javaMailSender;

    public ReportSchedulerImpl(ReportService reportService, JavaMailSender javaMailSender,
                               @Value("${application.email.sender}") String sender, @Value("${application.email.receiver}") String admin) {
        this.reportService = reportService;
        this.sender = sender;
        this.admin = admin;
        this.javaMailSender = javaMailSender;
    }

    /**
     * Triggers report generation and sending at 8AM of the first day of every month
     * Logs and handles exceptions
     */
    @Override
    @Scheduled(fixedDelay = 60_000)
    // @Scheduled(cron = "0 0 8 1 * *")
    public void sendMonthlyReport() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start = now.minusMonths(1);
            log.info("Generating email with document report for period {} - {}", start, now);
            sendReportBetween(start, now);
        } catch (MessagingException e) {
            log.error("An error occurred while sending monthly report: " + e.getMessage());
            throw new MailException(e);
        }
    }

    /**
     * Generates and sends document report for the period specified
     * and sends it within an email to the admin
     * @param start start of the period
     * @param end end of the period
     * @throws MessagingException throws up the stack if thrown by {@code MimeMessageHelper}
     */
    @Override
    public void sendReportBetween(LocalDateTime start, LocalDateTime end) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(new InternetAddress(sender));
        helper.setSubject("Щомісячний звіт");
        helper.setText("Звіт по документах за останній місяць");
        helper.setTo(admin);
        byte[] reportData = reportService.getReport(start, end);
        helper.addAttachment("звіт.xls",  new ByteArrayResource(reportData), "application-vnd/ms-excel");
        log.trace("The report data: " + new String(reportData));
        log.info("Sending email with document report for period {} - {}", start, end);
        javaMailSender.send(message);
    }

}