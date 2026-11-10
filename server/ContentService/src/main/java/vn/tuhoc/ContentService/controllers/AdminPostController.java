package vn.tuhoc.ContentService.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.ContentService.dtos.requests.PostUpdateModerationStatusRequestDTO;
import vn.tuhoc.ContentService.dtos.requests.PostUpdateStatusRequestDTO;
import vn.tuhoc.ContentService.dtos.responses.PostDetailResponseDTO;
import vn.tuhoc.ContentService.dtos.responses.PostSummaryResponseDTO;
import vn.tuhoc.ContentService.repositories.criteria.PostCriteria;
import vn.tuhoc.ContentService.services.interfaces.PostService;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;
import vn.tuhoc.core.dtos.responses.RestResponseDTO;
import vn.tuhoc.core.utils.RestResponseUtil;

@RestController
@RequestMapping("/api/v1/admin/posts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('ADMIN')")
public class AdminPostController {
    PostService postService;

    @GetMapping("")
    public ResponseEntity<RestResponseDTO<PageResponseDTO<PostSummaryResponseDTO>>> getSummary(
            PostCriteria postCriteria) {
        return RestResponseUtil.ok(
                "Truy vấn danh sách bài đăng thành công!",
                this.postService.handleGetSummary(postCriteria));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestResponseDTO<PostDetailResponseDTO>> getDetailById(
            @PathVariable("id") Integer id) {
        return RestResponseUtil.ok(
                "Truy vấn chi tiết bài đăng thành công!",
                this.postService.handleGetById(id));
    }

    @PatchMapping(value = "/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponseDTO<PostDetailResponseDTO>> updateStatusById(
            @PathVariable("id") Integer id,
            @RequestBody @Valid PostUpdateStatusRequestDTO request) {
        return RestResponseUtil.ok(
                "Cập nhật trạng thái bài đăng thành công!",
                this.postService.handleUpdateStatusById(id, request));
    }

    @PatchMapping(value = "/{id}/moderation-status", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponseDTO<PostDetailResponseDTO>> updateModerationStatusById(
            @PathVariable("id") Integer id,
            @RequestBody @Valid PostUpdateModerationStatusRequestDTO request) {
        return RestResponseUtil.ok(
                "Cập nhật kiểm duyệt bài đăng thành công!",
                this.postService.handleUpdateModerationStatusById(id, request));
    }
}
