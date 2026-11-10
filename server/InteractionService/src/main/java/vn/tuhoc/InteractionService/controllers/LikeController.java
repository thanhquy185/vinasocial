package vn.tuhoc.InteractionService.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.InteractionService.domains.enums.LikeTargetTypeEnum;
import vn.tuhoc.InteractionService.dtos.responses.InteractionActionResponseDTO;
import vn.tuhoc.InteractionService.dtos.responses.LikeSummaryResponseDTO;
import vn.tuhoc.InteractionService.services.interfaces.LikeService;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;
import vn.tuhoc.core.dtos.responses.RestResponseDTO;
import vn.tuhoc.core.utils.RestResponseUtil;

@RestController
@RequestMapping("/api/v1/interactions/likes")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LikeController {
    LikeService likeService;

    @PostMapping("/{targetType}/{targetId}")
    public ResponseEntity<RestResponseDTO<InteractionActionResponseDTO>> like(
            @PathVariable LikeTargetTypeEnum targetType,
            @PathVariable Integer targetId) {
        return RestResponseUtil.ok("Đã cập nhật trạng thái thích.", this.likeService.like(targetType, targetId));
    }

    @DeleteMapping("/{targetType}/{targetId}")
    public ResponseEntity<RestResponseDTO<InteractionActionResponseDTO>> unlike(
            @PathVariable LikeTargetTypeEnum targetType,
            @PathVariable Integer targetId) {
        return RestResponseUtil.ok("Đã cập nhật trạng thái thích.", this.likeService.unlike(targetType, targetId));
    }

    @GetMapping("/{targetType}/{targetId}/me")
    public ResponseEntity<RestResponseDTO<InteractionActionResponseDTO>> getStatus(
            @PathVariable LikeTargetTypeEnum targetType,
            @PathVariable Integer targetId) {
        return RestResponseUtil.ok("Truy vấn trạng thái thích thành công.",
                this.likeService.getLikeStatus(targetType, targetId));
    }

    @GetMapping("/{targetType}/{targetId}")
    public ResponseEntity<RestResponseDTO<PageResponseDTO<LikeSummaryResponseDTO>>> getLikes(
            @PathVariable LikeTargetTypeEnum targetType,
            @PathVariable Integer targetId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return RestResponseUtil.ok("Truy vấn danh sách lượt thích thành công.",
                this.likeService.getLikes(targetType, targetId, page, size));
    }
}
