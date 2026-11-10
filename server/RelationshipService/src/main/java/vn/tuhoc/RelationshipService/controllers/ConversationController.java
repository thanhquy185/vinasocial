package vn.tuhoc.RelationshipService.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.RelationshipService.dtos.requests.GroupConversationCreateRequestDTO;
import vn.tuhoc.RelationshipService.dtos.requests.ConversationParticipantRoleUpdateRequestDTO;
import vn.tuhoc.RelationshipService.dtos.responses.ConversationParticipantSummaryResponseDTO;
import vn.tuhoc.RelationshipService.dtos.responses.ConversationRequestSummaryResponseDTO;
import vn.tuhoc.RelationshipService.dtos.responses.ConversationSummaryResponseDTO;
import vn.tuhoc.RelationshipService.services.interfaces.MessagingService;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;
import vn.tuhoc.core.dtos.responses.RestResponseDTO;
import vn.tuhoc.core.utils.RestResponseUtil;

@RestController
@RequestMapping("/api/v1/conversations")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConversationController {
    MessagingService messagingService;

    @GetMapping("/me")
    public ResponseEntity<RestResponseDTO<PageResponseDTO<ConversationSummaryResponseDTO>>> getConversations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return RestResponseUtil.ok("Truy vấn danh sách cuộc trò chuyện thành công.",
                this.messagingService.getConversations(page, size));
    }

    @PatchMapping("/me/read")
    public ResponseEntity<RestResponseDTO<Integer>> markAllConversationsAsRead() {
        return RestResponseUtil.ok("Đã đánh dấu tất cả cuộc trò chuyện là đã đọc.",
                this.messagingService.markAllConversationsAsRead());
    }

    @PatchMapping("/{conversationId}/read")
    public ResponseEntity<RestResponseDTO<Integer>> markConversationAsRead(
            @PathVariable("conversationId") Integer conversationId) {
        return RestResponseUtil.ok("Đã đánh dấu cuộc trò chuyện là đã đọc.",
                this.messagingService.markConversationAsRead(conversationId));
    }

    @GetMapping("/me/requests")
    public ResponseEntity<RestResponseDTO<PageResponseDTO<ConversationRequestSummaryResponseDTO>>> getIncomingRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return RestResponseUtil.ok("Truy vấn lời mời nhắn tin thành công.",
                this.messagingService.getIncomingRequests(page, size));
    }

    @GetMapping("/me/sent-requests")
    public ResponseEntity<RestResponseDTO<PageResponseDTO<ConversationRequestSummaryResponseDTO>>> getOutgoingRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return RestResponseUtil.ok("Truy vấn yêu cầu nhắn tin đã gửi thành công.",
                this.messagingService.getOutgoingRequests(page, size));
    }

    @PostMapping("/requests/{requestId}/accept")
    public ResponseEntity<RestResponseDTO<ConversationRequestSummaryResponseDTO>> acceptRequest(
            @PathVariable("requestId") Integer requestId) {
        return RestResponseUtil.ok("Đã chấp nhận yêu cầu nhắn tin.",
                this.messagingService.acceptRequest(requestId));
    }

    @PostMapping("/requests/{requestId}/reject")
    public ResponseEntity<RestResponseDTO<ConversationRequestSummaryResponseDTO>> rejectRequest(
            @PathVariable("requestId") Integer requestId) {
        return RestResponseUtil.ok("Đã từ chối yêu cầu nhắn tin.",
                this.messagingService.rejectRequest(requestId));
    }

    @DeleteMapping("/requests/{requestId}")
    public ResponseEntity<RestResponseDTO<ConversationRequestSummaryResponseDTO>> cancelRequest(
            @PathVariable("requestId") Integer requestId) {
        return RestResponseUtil.ok("Đã hủy yêu cầu nhắn tin.",
                this.messagingService.cancelRequest(requestId));
    }

    @PostMapping("/groups")
    public ResponseEntity<RestResponseDTO<ConversationSummaryResponseDTO>> createGroup(
            @RequestBody @Valid GroupConversationCreateRequestDTO request) {
        return RestResponseUtil.created("Đã tạo cuộc trò chuyện nhóm.", this.messagingService.createGroup(request));
    }

    @GetMapping("/{conversationId}/participants")
    public ResponseEntity<RestResponseDTO<List<ConversationParticipantSummaryResponseDTO>>> getParticipants(
            @PathVariable("conversationId") Integer conversationId) {
        return RestResponseUtil.ok("Truy vấn thành viên cuộc trò chuyện thành công.",
                this.messagingService.getParticipants(conversationId));
    }

    @PostMapping("/{conversationId}/participants/{username}")
    public ResponseEntity<RestResponseDTO<ConversationSummaryResponseDTO>> addParticipant(
            @PathVariable("conversationId") Integer conversationId,
            @PathVariable("username") String username) {
        return RestResponseUtil.ok("Đã thêm thành viên vào nhóm.",
                this.messagingService.addParticipant(conversationId, username));
    }

    @PatchMapping("/{conversationId}/participants/{username}/role")
    public ResponseEntity<RestResponseDTO<ConversationSummaryResponseDTO>> updateParticipantRole(
            @PathVariable("conversationId") Integer conversationId,
            @PathVariable("username") String username,
            @RequestBody @Valid ConversationParticipantRoleUpdateRequestDTO request) {
        return RestResponseUtil.ok("Đã cập nhật vai trò thành viên.",
                this.messagingService.updateParticipantRole(conversationId, username, request.getRole()));
    }

    @DeleteMapping("/{conversationId}/participants/me")
    public ResponseEntity<RestResponseDTO<ConversationSummaryResponseDTO>> leaveConversation(
            @PathVariable("conversationId") Integer conversationId) {
        return RestResponseUtil.ok("Đã rời khỏi cuộc trò chuyện.",
                this.messagingService.leaveConversation(conversationId));
    }
}
