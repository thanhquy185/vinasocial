package vn.tuhoc.InteractionService.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.InteractionService.domains.enums.CommentTargetTypeEnum;
import vn.tuhoc.InteractionService.dtos.requests.CommentCreateRequestDTO;
import vn.tuhoc.InteractionService.dtos.requests.CommentStatusUpdateRequestDTO;
import vn.tuhoc.InteractionService.dtos.requests.CommentUpdateRequestDTO;
import vn.tuhoc.InteractionService.dtos.responses.CommentDetailResponseDTO;
import vn.tuhoc.InteractionService.services.interfaces.CommentService;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;
import vn.tuhoc.core.dtos.responses.RestResponseDTO;
import vn.tuhoc.core.utils.RestResponseUtil;

@RestController
@RequestMapping("/api/v1/interactions/comments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {
    CommentService commentService;

    @PostMapping
    public ResponseEntity<RestResponseDTO<CommentDetailResponseDTO>> create(
            @RequestBody @Valid CommentCreateRequestDTO request) {
        return RestResponseUtil.created("Đã tạo bình luận.", this.commentService.create(request));
    }

    @GetMapping
    public ResponseEntity<RestResponseDTO<PageResponseDTO<CommentDetailResponseDTO>>> getComments(
            @RequestParam CommentTargetTypeEnum targetType,
            @RequestParam Integer targetId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return RestResponseUtil.ok("Truy vấn danh sách bình luận thành công.",
                this.commentService.getComments(targetType, targetId, page, size));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<RestResponseDTO<CommentDetailResponseDTO>> getById(@PathVariable Integer commentId) {
        return RestResponseUtil.ok("Truy vấn bình luận thành công.", this.commentService.getById(commentId));
    }

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<RestResponseDTO<PageResponseDTO<CommentDetailResponseDTO>>> getReplies(
            @PathVariable Integer commentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return RestResponseUtil.ok("Truy vấn danh sách trả lời thành công.",
                this.commentService.getReplies(commentId, page, size));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<RestResponseDTO<CommentDetailResponseDTO>> update(
            @PathVariable Integer commentId,
            @RequestBody @Valid CommentUpdateRequestDTO request) {
        return RestResponseUtil.ok("Đã cập nhật bình luận.", this.commentService.update(commentId, request));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<RestResponseDTO<CommentDetailResponseDTO>> delete(@PathVariable Integer commentId) {
        return RestResponseUtil.ok("Đã gỡ bình luận.", this.commentService.delete(commentId));
    }

    @PatchMapping("/{commentId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestResponseDTO<CommentDetailResponseDTO>> updateStatus(
            @PathVariable Integer commentId,
            @RequestBody @Valid CommentStatusUpdateRequestDTO request) {
        return RestResponseUtil.ok("Đã cập nhật trạng thái bình luận.",
                this.commentService.updateStatus(commentId, request.getStatus()));
    }
}
