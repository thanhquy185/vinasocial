package vn.tuhoc.ContentService.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.ContentService.dtos.requests.PostCreateRequestDTO;
import vn.tuhoc.ContentService.dtos.requests.PostUpdateRequestDTO;
import vn.tuhoc.ContentService.dtos.responses.PostDetailResponseDTO;
import vn.tuhoc.ContentService.dtos.responses.PostSummaryResponseDTO;
import vn.tuhoc.ContentService.dtos.responses.ProfilePostResponseDTO;
import vn.tuhoc.ContentService.repositories.criteria.PostCriteria;
import vn.tuhoc.ContentService.services.interfaces.PostService;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;
import vn.tuhoc.core.dtos.responses.RestResponseDTO;
import vn.tuhoc.core.utils.RestResponseUtil;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostController {
    PostService postService;

    @GetMapping("/profile/{username}")
    public ResponseEntity<RestResponseDTO<java.util.List<ProfilePostResponseDTO>>> getProfilePosts(
            @PathVariable("username") String username) {
        return RestResponseUtil.ok("Truy vấn bài viết trang cá nhân thành công!",
                this.postService.handleGetProfilePosts(username));
    }

    @GetMapping("/profile/{username}/tagged")
    public ResponseEntity<RestResponseDTO<java.util.List<PostDetailResponseDTO>>> getTaggedPosts(
            @PathVariable("username") String username) {
        return RestResponseUtil.ok("Truy vấn bài viết được gắn thẻ thành công!",
                this.postService.handleGetTaggedPosts(username));
    }

    @GetMapping("/explore")
    public ResponseEntity<RestResponseDTO<PageResponseDTO<PostDetailResponseDTO>>> getExplorePosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Boolean reelsOnly) {
        return RestResponseUtil.ok("Truy vấn nội dung khám phá thành công.",
                this.postService.handleGetExplorePosts(page, size, query, reelsOnly));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestResponseDTO<PostDetailResponseDTO>> getDetailById(@PathVariable("id") Integer id) {
        PostDetailResponseDTO postDetail = this.postService.handleGetById(id);

        return RestResponseUtil.ok(
                "Truy vấn bài đăng theo mã bài đăng thành công!",
                postDetail);
    }

    @GetMapping("")
    public ResponseEntity<RestResponseDTO<PageResponseDTO<PostSummaryResponseDTO>>> getSummary(
            PostCriteria postCriteria) {
        PageResponseDTO<PostSummaryResponseDTO> postSummary = this.postService.handleGetSummary(postCriteria);

        return RestResponseUtil.ok(
                "Truy vấn danh sách bài đăng thành công!",
                postSummary);
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponseDTO<PostDetailResponseDTO>> create(
            @RequestBody @Valid PostCreateRequestDTO postCreateRequestDTO) {
        PostDetailResponseDTO postCreated = this.postService.handleCreate(postCreateRequestDTO);

        return RestResponseUtil.created(
                "Thêm bài đăng thành công!",
                postCreated);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponseDTO<PostDetailResponseDTO>> updateById(
            @PathVariable("id") Integer id,
            @RequestBody @Valid PostUpdateRequestDTO request) {
        PostDetailResponseDTO postUpdated = this.postService.handleUpdateById(id, request);

        return RestResponseUtil.ok("Cập nhật bài đăng thành công!", postUpdated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RestResponseDTO<Void>> removeById(@PathVariable("id") Integer id) {
        this.postService.handleRemove(id);

        return RestResponseUtil.ok("Đã xóa bài đăng thành công!", null);
    }
}
