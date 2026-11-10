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
import vn.tuhoc.InteractionService.dtos.responses.InteractionActionResponseDTO;
import vn.tuhoc.InteractionService.dtos.responses.PostCollectionSummaryResponseDTO;
import vn.tuhoc.InteractionService.services.interfaces.ReupService;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;
import vn.tuhoc.core.dtos.responses.RestResponseDTO;
import vn.tuhoc.core.utils.RestResponseUtil;

@RestController
@RequestMapping("/api/v1/interactions/reups")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReupController {
    ReupService reupService;

    @PostMapping("/{postId}")
    public ResponseEntity<RestResponseDTO<InteractionActionResponseDTO>> reup(@PathVariable Integer postId) {
        return RestResponseUtil.ok("Đã cập nhật trạng thái đăng lại.", this.reupService.reup(postId));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<RestResponseDTO<InteractionActionResponseDTO>> unreup(@PathVariable Integer postId) {
        return RestResponseUtil.ok("Đã cập nhật trạng thái đăng lại.", this.reupService.unreup(postId));
    }

    @GetMapping("/me")
    public ResponseEntity<RestResponseDTO<PageResponseDTO<PostCollectionSummaryResponseDTO>>> getMyReups(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return RestResponseUtil.ok("Truy vấn danh sách bài viết đã đăng lại thành công.",
                this.reupService.getMyReups(page, size));
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<RestResponseDTO<PageResponseDTO<PostCollectionSummaryResponseDTO>>> getUserReups(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return RestResponseUtil.ok("Truy vấn danh sách bài viết đã đăng lại thành công.",
                this.reupService.getReupsByUsername(username, page, size));
    }
}
