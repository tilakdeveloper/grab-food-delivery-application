package com.grab.FoodApp.email_notification.services;

import com.grab.FoodApp.email_notification.dtos.NotificationDTO;

public interface NotificationService {
    void sendEmail(NotificationDTO notificationDTO);
}
