package vn.tuhoc.NotificationService.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.NotificationService.dtos.responses.NotificationActionResponseDTO;
import vn.tuhoc.NotificationService.dtos.responses.NotificationResponseDTO;
import vn.tuhoc.NotificationService.dtos.responses.NotificationUnreadCountResponseDTO;
import vn.tuhoc.NotificationService.dtos.requests.UserTestConnectRequestDTO;
import vn.tuhoc.NotificationService.services.interfaces.NotificationService;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;
import vn.tuhoc.core.dtos.responses.RestResponseDTO;
import vn.tuhoc.core.utils.RestResponseUtil;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {
    NotificationService notificationService;

    @GetMapping("/me")
    public ResponseEntity<RestResponseDTO<PageResponseDTO<NotificationResponseDTO>>> getMine(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "false") boolean unreadOnly) {
        return RestResponseUtil.ok("Truy vấn thông báo thành công.",
                this.notificationService.getMine(page, size, unreadOnly));
    }

    @GetMapping("/me/unread-count")
    public ResponseEntity<RestResponseDTO<NotificationUnreadCountResponseDTO>> getUnreadCount() {
        return RestResponseUtil.ok("Truy vấn số thông báo chưa đọc thành công.",
                new NotificationUnreadCountResponseDTO(this.notificationService.getUnreadCount()));
    }

    @PatchMapping("/me/{notificationId}/read")
    public ResponseEntity<RestResponseDTO<NotificationResponseDTO>> markAsRead(
            @PathVariable String notificationId) {
        return RestResponseUtil.ok("Đã đánh dấu thông báo đã đọc.",
                this.notificationService.markAsRead(notificationId));
    }

    @PatchMapping("/me/read-all")
    public ResponseEntity<RestResponseDTO<NotificationActionResponseDTO>> markAllAsRead() {
        return RestResponseUtil.ok("Đã đánh dấu các thông báo là đã đọc.",
                this.notificationService.markAllAsRead());
    }

    @DeleteMapping("/me/{notificationId}")
    public ResponseEntity<RestResponseDTO<NotificationResponseDTO>> dismiss(
            @PathVariable String notificationId) {
        return RestResponseUtil.ok("Đã ẩn thông báo.", this.notificationService.dismiss(notificationId));
    }

    // Retained because UserService still calls this endpoint in its connectivity smoke check.
    @PostMapping("/notification-test-connect")
    public ResponseEntity<RestResponseDTO<String>> notificationTestConnect(
            @RequestBody UserTestConnectRequestDTO userTestConnect) {
        return RestResponseUtil.ok("Kết nối NotificationService thành công.",
                this.notificationService.notificationTestConnect(userTestConnect));
    }
}
