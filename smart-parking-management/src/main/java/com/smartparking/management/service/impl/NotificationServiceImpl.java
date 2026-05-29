package com.smartparking.management.service.impl;

import com.smartparking.management.dto.response.NotificationResponse;
import com.smartparking.management.entity.Notification;
import com.smartparking.management.entity.User;
import com.smartparking.management.enums.NotificationType;
import com.smartparking.management.exceptions.BadRequestException;
import com.smartparking.management.exceptions.ResourceNotFoundException;
import com.smartparking.management.mapper.NotificationMapper;
import com.smartparking.management.repository.NotificationRepository;
import com.smartparking.management.repository.UserRepository;
import com.smartparking.management.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void createNotification(User user, String message, NotificationType notificationType) {

        Notification notification = new Notification();

        notification.setUser(user);
        notification.setMessage(message);
        notification.setNotificationType(notificationType);
        notification.setIsRead(false);

        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationResponse> getMyNotifications() {
        User user = getLoggedInUser();

        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(NotificationMapper::mapToNotificationResponse)
                .toList();
    }

    @Override
    public Long getMyUnreadNotificationCount() {
        User user = getLoggedInUser();

        return notificationRepository
                .countByUserIdAndIsReadFalse(user.getId());
    }

    @Override
    public NotificationResponse markAsRead(Long notificationId) {
        User user = getLoggedInUser();

        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You cannot access this notification");
        }

        notification.setIsRead(true);
        Notification saved = notificationRepository.save(notification);
        return NotificationMapper.mapToNotificationResponse(saved);
    }

    private User getLoggedInUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository
                .findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
