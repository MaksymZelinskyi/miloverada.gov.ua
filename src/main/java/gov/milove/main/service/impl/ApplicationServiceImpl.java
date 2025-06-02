package gov.milove.main.service.impl;

import static gov.milove.main.constants.Constants.APPLICATION_NOTIFY_DESTINATION;

import gov.milove.main.dto.request.ApplicationCreateRequest;
import gov.milove.main.service.ApplicationService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
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
  private final SimpMessagingTemplate messagingTemplate;

  @Value("${application.receiverEmail}")
  private String APPLICATION_RECEIVER_EMAIL;

  private final String SUBJECT_PREFIX = "Звернення від ";

  @Override
  @Async
  public void createApplication(ApplicationCreateRequest request, List<ByteArrayResource> inputStreamResourceList) {
    log.info("Create application, username: {}", request.fullName());
    log.info(request.phoneNumber());
    String htmlContent = generateEmailContent(request);

    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setTo(APPLICATION_RECEIVER_EMAIL);
      helper.setSubject(SUBJECT_PREFIX + request.fullName());
      helper.setText(htmlContent, true);

      addAttachments(helper, inputStreamResourceList);

      mailSender.send(message);

      messagingTemplate.convertAndSend(APPLICATION_NOTIFY_DESTINATION.formatted(request.tempNotificationDestination()),
          "true");
    } catch (MessagingException e) {
      log.error("Error when send message", e);
    }
  }

  private void addAttachments(MimeMessageHelper helper,  List<ByteArrayResource> inputStreamResourceList)
      throws MessagingException {
    for (ByteArrayResource inputStreamResource : inputStreamResourceList) {
      log.info("add file as attachment: {}", inputStreamResource.getFilename());
      helper.addAttachment(inputStreamResource.getFilename(), inputStreamResource);
    }
  }

  private String generateEmailContent(ApplicationCreateRequest request) {
    Context context = new Context();
    context.setVariable("name", request.fullName());
    context.setVariable("phone", request.phoneNumber());
    context.setVariable("email", request.email());
    context.setVariable("applicationText", request.applicationText());

    return templateEngine.process("application", context);
  }
}
