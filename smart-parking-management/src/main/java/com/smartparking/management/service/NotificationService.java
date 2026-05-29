package com.smartparking.management.service;

import com.smartparking.management.dto.response.NotificationResponse;
import com.smartparking.management.entity.User;
import com.smartparking.management.enums.NotificationType;

import java.util.List;

public interface NotificationService {

    void createNotification(User user, String message, NotificationType notificationType);

    List<NotificationResponse> getMyNotifications();
    Long getMyUnreadNotificationCount();
    NotificationResponse markAsRead(Long notificationId);
}
