package com.smartparking.management.mapper;

import com.smartparking.management.dto.response.NotificationResponse;
import com.smartparking.management.entity.Notification;

public class NotificationMapper {

    public static NotificationResponse mapToNotificationResponse(Notification notification) {

        NotificationResponse response = new NotificationResponse();

        response.setId(notification.getId());
        response.setMessage(notification.getMessage());
        response.setNotificationType(notification.getNotificationType());
        response.setIsRead(notification.getIsRead());
        response.setCreatedAt(notification.getCreatedAt());

        return response;
    }
}
