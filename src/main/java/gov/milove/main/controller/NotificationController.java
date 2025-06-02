package gov.milove.main.controller;

import gov.milove.main.domain.Notification;
import gov.milove.main.dto.NewNotificationDto;
import gov.milove.main.dto.NotificationDtoWithViews;
import io.swagger.v3.oas.annotations .Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Notification controller")
public interface NotificationController {

    @Operation(summary = "Get the number of actual notifications")
    Long getTotalNumberOfActualNotifications(@RequestParam String encodedUserId);

    @Operation(summary = "Get all notifications")
    List<NotificationDtoWithViews> getAll(@RequestParam String encodedUserId);

    @Operation(summary = "Get notification by id")
    Notification getById(@PathVariable Long id, @RequestParam Boolean isViewed, @RequestParam String encodedUserId);

    @Operation(summary = "Create new notification")
    Notification createNew(@RequestBody NewNotificationDto n);

    @Operation(summary = "Edit notification")
    Notification createNew(@RequestBody NewNotificationDto n, @PathVariable Long id);

    @Operation(summary = "Delete notification")
    Long createNew(@PathVariable Long id);
}
