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
import vn.tuhoc.InteractionService.services.interfaces.SaveService;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;
import vn.tuhoc.core.dtos.responses.RestResponseDTO;
import vn.tuhoc.core.utils.RestResponseUtil;

@RestController
@RequestMapping("/api/v1/interactions/saves")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SaveController {
    SaveService saveService;

    @PostMapping("/{postId}")
    public ResponseEntity<RestResponseDTO<InteractionActionResponseDTO>> save(@PathVariable Integer postId) {
        return RestResponseUtil.ok("Đã cập nhật danh sách đã lưu.", this.saveService.save(postId));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<RestResponseDTO<InteractionActionResponseDTO>> unsave(@PathVariable Integer postId) {
        return RestResponseUtil.ok("Đã cập nhật danh sách đã lưu.", this.saveService.unsave(postId));
    }

    @GetMapping("/{postId}/me")
    public ResponseEntity<RestResponseDTO<InteractionActionResponseDTO>> getStatus(@PathVariable Integer postId) {
        return RestResponseUtil.ok("Truy vấn trạng thái lưu thành công.", this.saveService.getSaveStatus(postId));
    }

    @GetMapping("/me")
    public ResponseEntity<RestResponseDTO<PageResponseDTO<PostCollectionSummaryResponseDTO>>> getMySaves(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return RestResponseUtil.ok("Truy vấn danh sách bài viết đã lưu thành công.",
                this.saveService.getMySaves(page, size));
    }
}
