package gov.milove.main.service.impl;

import gov.milove.main.dto.request.ApplicationCreateRequest;
import gov.milove.main.service.ApplicationService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @author Liashenko Andrii
 * @since 3/5/2025
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  @Override
  @Async
  public void createApplication(ApplicationCreateRequest request) {
    log.info("Create application, username: {}", request.fullName());
    Context context = new Context();
    context.setVariable("name", request.fullName());
    context.setVariable("phone", request.phoneNumber());
    context.setVariable("email", request.email());
    context.setVariable("applicationText", request.applicationText());

    String htmlContent = templateEngine.process("application", context);

    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setTo("a.liashenko@e-u.edu.ua");
      helper.setSubject("Звернення від " + request.fullName());
      helper.setText(htmlContent, true);

      if (request.files() != null) {
        for (MultipartFile file : request.files()) {
          if (!file.isEmpty()) {
            helper.addAttachment(file.getOriginalFilename(), file);
          }
        }
      }
      mailSender.send(message);

      log.info("After send");
    } catch (MessagingException e) {
      log.error("Error when send message", e);
    }
  }
}
