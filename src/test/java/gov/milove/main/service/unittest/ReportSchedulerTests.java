package gov.milove.main.service.unittest;

import gov.milove.main.service.ReportService;
import gov.milove.main.service.impl.ReportSchedulerImpl;

import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

@ExtendWith(SpringExtension.class)
class ReportSchedulerTests {

    @InjectMocks
    private ReportSchedulerImpl scheduler;

    @Mock
    private ReportService reportService;

    @Mock
    private JavaMailSender mailSender;

    @Captor
    private ArgumentCaptor<MimeMessage> captor;

    @Test
    void testSendMonthlyReportBetween() throws Exception {
        ReflectionTestUtils.setField(scheduler, "sender", "mocked@example.com");
        ReflectionTestUtils.setField(scheduler, "admin", "admin@example.com");
        byte[] pdfData = "fake-pdf-data".getBytes();
        LocalDateTime start = LocalDateTime.of(2024, 5, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 6, 1, 0, 0);

        Mockito.when(reportService.getReport(start, end)).thenReturn(pdfData);
        Mockito.when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

        scheduler.sendReportBetween(start, end);

        Mockito.verify(reportService).getReport(start, end);

        Mockito.verify(mailSender).send(captor.capture());
        MimeMessage sent = captor.getValue();
        assertEquals("mocked@example.com", ((InternetAddress) sent.getFrom()[0]).getAddress());
        assertEquals("admin@example.com", sent.getAllRecipients()[0].toString());
    }
}