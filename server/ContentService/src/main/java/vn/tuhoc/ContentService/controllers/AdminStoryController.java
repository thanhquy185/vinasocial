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
import vn.tuhoc.ContentService.dtos.requests.StoryUpdateModerationStatusRequestDTO;
import vn.tuhoc.ContentService.dtos.requests.StoryUpdateStatusRequestDTO;
import vn.tuhoc.ContentService.dtos.responses.StoryDetailResponseDTO;
import vn.tuhoc.ContentService.dtos.responses.StorySummaryResponseDTO;
import vn.tuhoc.ContentService.repositories.criteria.StoryCriteria;
import vn.tuhoc.ContentService.services.interfaces.StoryService;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;
import vn.tuhoc.core.dtos.responses.RestResponseDTO;
import vn.tuhoc.core.utils.RestResponseUtil;

@RestController
@RequestMapping("/api/v1/admin/stories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('ADMIN')")
public class AdminStoryController {
    StoryService storyService;

    @GetMapping("")
    public ResponseEntity<RestResponseDTO<PageResponseDTO<StorySummaryResponseDTO>>> getSummary(
            StoryCriteria storyCriteria) {
        PageResponseDTO<StorySummaryResponseDTO> storySummary = this.storyService.handleGetSummary(storyCriteria);

        return RestResponseUtil.ok(
                "Truy vấn danh sách câu chuyện thành công!",
                storySummary);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestResponseDTO<StoryDetailResponseDTO>> getDetailById(
            @PathVariable("id") Integer id) {
        StoryDetailResponseDTO storyDetail = this.storyService.handleGetById(id);

        return RestResponseUtil.ok(
                "Truy vấn chi tiết câu chuyện thành công!",
                storyDetail);
    }

    @PatchMapping(value = "/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponseDTO<StoryDetailResponseDTO>> updateStatusById(
            @PathVariable("id") Integer id,
            @RequestBody @Valid StoryUpdateStatusRequestDTO request) {
        StoryDetailResponseDTO storyUpdated = this.storyService.handleUpdateStatusById(id, request);

        return RestResponseUtil.ok(
                "Cập nhật trạng thái câu chuyện thành công!",
                storyUpdated);
    }

    @PatchMapping(value = "/{id}/moderation-status", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponseDTO<StoryDetailResponseDTO>> updateModerationStatusById(
            @PathVariable("id") Integer id,
            @RequestBody @Valid StoryUpdateModerationStatusRequestDTO request) {
        StoryDetailResponseDTO storyUpdated = this.storyService.handleUpdateModerationStatusById(id, request);

        return RestResponseUtil.ok(
                "Cập nhật kiểm duyệt câu chuyện thành công!",
                storyUpdated);
    }
}
