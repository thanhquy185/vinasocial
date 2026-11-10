package vn.tuhoc.RelationshipService.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.RelationshipService.dtos.requests.MessageCreateRequestDTO;
import vn.tuhoc.RelationshipService.dtos.requests.MessageUpdateRequestDTO;
import vn.tuhoc.RelationshipService.dtos.responses.DirectMessageResponseDTO;
import vn.tuhoc.RelationshipService.dtos.responses.MessageDetailResponseDTO;
import vn.tuhoc.RelationshipService.services.interfaces.MessagingService;
import vn.tuhoc.RelationshipService.services.MessageEncryptionSettingService;
import vn.tuhoc.security.utils.SecurityUtil;
import jakarta.validation.constraints.NotNull;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;
import vn.tuhoc.core.dtos.responses.RestResponseDTO;
import vn.tuhoc.core.utils.RestResponseUtil;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessageController {
    MessagingService messagingService;
    MessageEncryptionSettingService encryptionSettingService;

    public record EncryptionSettingRequest(@NotNull Boolean enabled) {}

    @GetMapping("/conversations/me/encryption-setting")
    public ResponseEntity<RestResponseDTO<Boolean>> getEncryptionSetting() {
        Integer userId = SecurityUtil.getCurrentUserId().orElseThrow();
        return RestResponseUtil.ok("Đã lấy chế độ mã hóa.", encryptionSettingService.isEnabled(userId));
    }

    @PutMapping("/conversations/me/encryption-setting")
    public ResponseEntity<RestResponseDTO<Boolean>> updateEncryptionSetting(
            @RequestBody @Valid EncryptionSettingRequest request) {
        Integer userId = SecurityUtil.getCurrentUserId().orElseThrow();
        return RestResponseUtil.ok("Đã cập nhật chế độ mã hóa.",
                encryptionSettingService.setEnabled(userId, request.enabled()));
    }

    @PostMapping("/users/{username}/messages")
    public ResponseEntity<RestResponseDTO<DirectMessageResponseDTO>> sendDirectMessage(
            @PathVariable("username") String username,
            @RequestBody @Valid MessageCreateRequestDTO request) {
        DirectMessageResponseDTO response = this.messagingService.sendDirectMessage(username, request);
        String message = "REQUESTED".equals(response.getConversationState())
                ? "Đã gửi yêu cầu nhắn tin."
                : "Đã gửi tin nhắn.";
        return RestResponseUtil.created(message, response);
    }

    @PostMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<RestResponseDTO<MessageDetailResponseDTO>> sendConversationMessage(
            @PathVariable("conversationId") Integer conversationId,
            @RequestBody @Valid MessageCreateRequestDTO request) {
        return RestResponseUtil.created("Đã gửi tin nhắn.",
                this.messagingService.sendConversationMessage(conversationId, request));
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<RestResponseDTO<PageResponseDTO<MessageDetailResponseDTO>>> getMessages(
            @PathVariable("conversationId") Integer conversationId,
            @RequestParam(required = false) Integer beforeId,
            @RequestParam(defaultValue = "50") int size) {
        return RestResponseUtil.ok("Truy vấn tin nhắn thành công.",
                this.messagingService.getMessages(conversationId, beforeId, size));
    }

    @PatchMapping("/conversations/{conversationId}/messages/{messageId}")
    public ResponseEntity<RestResponseDTO<MessageDetailResponseDTO>> editMessage(
            @PathVariable("conversationId") Integer conversationId,
            @PathVariable("messageId") Integer messageId,
            @RequestBody @Valid MessageUpdateRequestDTO request) {
        return RestResponseUtil.ok("Đã cập nhật tin nhắn.",
                this.messagingService.editMessage(conversationId, messageId, request));
    }

    @DeleteMapping("/conversations/{conversationId}/messages/{messageId}")
    public ResponseEntity<RestResponseDTO<MessageDetailResponseDTO>> deleteMessage(
            @PathVariable("conversationId") Integer conversationId,
            @PathVariable("messageId") Integer messageId) {
        return RestResponseUtil.ok("Đã xóa tin nhắn.",
                this.messagingService.deleteMessage(conversationId, messageId));
    }
}
