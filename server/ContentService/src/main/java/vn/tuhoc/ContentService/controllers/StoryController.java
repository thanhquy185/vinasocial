package vn.tuhoc.ContentService.controllers;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.ContentService.dtos.requests.StoryCreateRequestDTO;
import vn.tuhoc.ContentService.dtos.requests.StorySettingUpdateRequestDTO;
import vn.tuhoc.ContentService.dtos.requests.StoryHighlightRequestDTO;
import vn.tuhoc.ContentService.dtos.responses.StoryDetailResponseDTO;
import vn.tuhoc.ContentService.dtos.responses.StorySummaryResponseDTO;
import vn.tuhoc.ContentService.dtos.responses.StoryHighlightResponseDTO;
import vn.tuhoc.ContentService.services.interfaces.StoryService;
import vn.tuhoc.core.dtos.responses.RestResponseDTO;
import vn.tuhoc.core.utils.RestResponseUtil;

@RestController
@RequestMapping("/api/v1/stories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StoryController {
    StoryService storyService;

    @GetMapping("/feed")
    public ResponseEntity<RestResponseDTO<List<StorySummaryResponseDTO>>> getFeed() {
        return RestResponseUtil.ok("Truy vấn tin mới thành công.", this.storyService.handleGetFeed());
    }

    @GetMapping("/me")
    public ResponseEntity<RestResponseDTO<List<StorySummaryResponseDTO>>> getMyStories() {
        return RestResponseUtil.ok("Truy vấn tin của tôi thành công.", this.storyService.handleGetByUsername(null));
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<RestResponseDTO<List<StorySummaryResponseDTO>>> getUserStories(
            @PathVariable("username") String username) {
        return RestResponseUtil.ok("Truy vấn tin người dùng thành công.", this.storyService.handleGetByUsername(username));
    }

    @GetMapping("/highlights/users/{username}")
    public ResponseEntity<RestResponseDTO<List<StoryHighlightResponseDTO>>> getHighlights(
            @PathVariable("username") String username) {
        return RestResponseUtil.ok("Truy vấn tin nổi bật thành công.", this.storyService.handleGetHighlights(username));
    }

    @GetMapping("/highlights/me/stories")
    public ResponseEntity<RestResponseDTO<List<StorySummaryResponseDTO>>> getHighlightableStories() {
        return RestResponseUtil.ok("Truy vấn kho tin thành công.", this.storyService.handleGetHighlightableStories());
    }

    @PostMapping(value = "/highlights", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponseDTO<StoryHighlightResponseDTO>> createHighlight(
            @RequestBody @Valid StoryHighlightRequestDTO request) {
        return RestResponseUtil.created("Tạo tin nổi bật thành công.", this.storyService.handleCreateHighlight(request));
    }

    @PutMapping(value = "/highlights/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponseDTO<StoryHighlightResponseDTO>> updateHighlight(
            @PathVariable Integer id,
            @RequestBody @Valid StoryHighlightRequestDTO request) {
        return RestResponseUtil.ok("Cập nhật tin nổi bật thành công.", this.storyService.handleUpdateHighlight(id, request));
    }

    @DeleteMapping("/highlights/{id}")
    public ResponseEntity<RestResponseDTO<Void>> removeHighlight(@PathVariable Integer id) {
        this.storyService.handleRemoveHighlight(id);
        return RestResponseUtil.ok("Đã xóa tin nổi bật.", null);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponseDTO<StoryDetailResponseDTO>> create(
            @RequestBody @Valid StoryCreateRequestDTO request) {
        return RestResponseUtil.created("Đăng tin thành công.", this.storyService.handleCreate(request));
    }

    @PutMapping(value = "/{id}/settings", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponseDTO<StoryDetailResponseDTO>> updateSettings(
            @PathVariable Integer id,
            @RequestBody @Valid StorySettingUpdateRequestDTO request) {
        return RestResponseUtil.ok("Cập nhật cài đặt tin thành công.", this.storyService.handleUpdateSetting(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RestResponseDTO<Void>> remove(@PathVariable Integer id) {
        this.storyService.handleRemove(id);
        return RestResponseUtil.ok("Đã xoá tin.", null);
    }
}
