package com.cts.client;

import com.cts.dtos.NotificationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", url = "${notification.service.url}")
public interface NotificationServiceClient {

    @PostMapping("/api/notifications")
    void sendNotification(@RequestBody NotificationDto notificationDto);
}
