package vn.tuhoc.NotificationService.services.interfaces;

import vn.tuhoc.NotificationService.dtos.events.NotificationCreatedEventDTO;
import vn.tuhoc.NotificationService.dtos.events.NotificationPreferencesUpdatedEventDTO;
import vn.tuhoc.NotificationService.dtos.responses.NotificationActionResponseDTO;
import vn.tuhoc.NotificationService.dtos.responses.NotificationResponseDTO;
import vn.tuhoc.NotificationService.dtos.requests.UserTestConnectRequestDTO;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;

public interface NotificationService {
    String notificationTestConnect(UserTestConnectRequestDTO userTestConnect);

    PageResponseDTO<NotificationResponseDTO> getMine(int page, int size, boolean unreadOnly);

    long getUnreadCount();

    NotificationResponseDTO markAsRead(String notificationId);

    NotificationActionResponseDTO markAllAsRead();

    NotificationResponseDTO dismiss(String notificationId);

    void handleCreatedEvent(NotificationCreatedEventDTO event);

    void handlePreferencesUpdatedEvent(NotificationPreferencesUpdatedEventDTO event);
}
