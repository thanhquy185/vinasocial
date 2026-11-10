package vn.tuhoc.RelationshipService.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.RelationshipService.dtos.responses.CloseFriendSummaryResponseDTO;
import vn.tuhoc.RelationshipService.dtos.responses.BlockSummaryResponseDTO;
import vn.tuhoc.RelationshipService.dtos.responses.FollowRequestSummaryResponseDTO;
import vn.tuhoc.RelationshipService.dtos.responses.FollowSummaryResponseDTO;
import vn.tuhoc.RelationshipService.dtos.responses.RelationshipActionResponseDTO;
import vn.tuhoc.RelationshipService.dtos.responses.RelationshipStatusResponseDTO;
import vn.tuhoc.RelationshipService.services.interfaces.RelationshipService;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;
import vn.tuhoc.core.dtos.responses.RestResponseDTO;
import vn.tuhoc.core.utils.RestResponseUtil;

@RestController
@RequestMapping("/api/v1/relationships")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RelationshipController {
    RelationshipService relationshipService;

    @PostMapping("/users/{username}/follow")
    public ResponseEntity<RestResponseDTO<RelationshipActionResponseDTO>> follow(
            @PathVariable("username") String username) {
        return RestResponseUtil.ok("Đã cập nhật trạng thái theo dõi.", this.relationshipService.follow(username));
    }

    @DeleteMapping("/users/{username}/follow")
    public ResponseEntity<RestResponseDTO<RelationshipActionResponseDTO>> unfollowOrCancel(
            @PathVariable("username") String username) {
        return RestResponseUtil.ok("Đã cập nhật trạng thái theo dõi.",
                this.relationshipService.unfollowOrCancel(username));
    }

    @GetMapping("/me/followers")
    public ResponseEntity<RestResponseDTO<PageResponseDTO<FollowSummaryResponseDTO>>> getFollowers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return RestResponseUtil.ok("Truy vấn danh sách người theo dõi thành công.",
                this.relationshipService.getFollowers(page, size));
    }

    @GetMapping("/me/following")
    public ResponseEntity<RestResponseDTO<PageResponseDTO<FollowSummaryResponseDTO>>> getFollowing(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return RestResponseUtil.ok("Truy vấn danh sách đang theo dõi thành công.",
                this.relationshipService.getFollowing(page, size));
    }

    @GetMapping("/me/follow-requests")
    public ResponseEntity<RestResponseDTO<PageResponseDTO<FollowRequestSummaryResponseDTO>>> getFollowRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return RestResponseUtil.ok("Truy vấn yêu cầu theo dõi thành công.",
                this.relationshipService.getIncomingFollowRequests(page, size));
    }

    @PostMapping("/follow-requests/{requestId}/accept")
    public ResponseEntity<RestResponseDTO<RelationshipActionResponseDTO>> acceptFollowRequest(
            @PathVariable("requestId") Integer requestId) {
        return RestResponseUtil.ok("Đã chấp nhận yêu cầu theo dõi.",
                this.relationshipService.acceptFollowRequest(requestId));
    }

    @PostMapping("/follow-requests/{requestId}/reject")
    public ResponseEntity<RestResponseDTO<RelationshipActionResponseDTO>> rejectFollowRequest(
            @PathVariable("requestId") Integer requestId) {
        return RestResponseUtil.ok("Đã từ chối yêu cầu theo dõi.",
                this.relationshipService.rejectFollowRequest(requestId));
    }

    @DeleteMapping("/follow-requests/{requestId}")
    public ResponseEntity<RestResponseDTO<RelationshipActionResponseDTO>> cancelFollowRequest(
            @PathVariable("requestId") Integer requestId) {
        return RestResponseUtil.ok("Đã hủy yêu cầu theo dõi.",
                this.relationshipService.cancelFollowRequest(requestId));
    }

    @GetMapping("/users/{username}/relationship")
    public ResponseEntity<RestResponseDTO<RelationshipStatusResponseDTO>> getRelationshipStatus(
            @PathVariable("username") String username) {
        return RestResponseUtil.ok("Truy vấn trạng thái quan hệ thành công.",
                this.relationshipService.getRelationshipStatus(username));
    }

    @PostMapping("/me/story-audience/{visibility}")
    public ResponseEntity<RestResponseDTO<List<Integer>>> getStoryAudience(
            @PathVariable("visibility") String visibility,
            @RequestBody List<Integer> ownerUserIds) {
        return RestResponseUtil.ok("Đã kiểm tra quyền xem tin.",
                this.relationshipService.getStoryAudience(visibility, ownerUserIds));
    }

    @GetMapping("/me/close-friends")
    public ResponseEntity<RestResponseDTO<PageResponseDTO<CloseFriendSummaryResponseDTO>>> getCloseFriends(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return RestResponseUtil.ok("Truy vấn danh sách bạn thân thành công.",
                this.relationshipService.getCloseFriends(page, size));
    }

    @PutMapping("/me/close-friends/{username}")
    public ResponseEntity<RestResponseDTO<RelationshipActionResponseDTO>> addCloseFriend(
            @PathVariable("username") String username) {
        return RestResponseUtil.ok("Đã thêm vào danh sách bạn thân.",
                this.relationshipService.addCloseFriend(username));
    }

    @DeleteMapping("/me/close-friends/{username}")
    public ResponseEntity<RestResponseDTO<RelationshipActionResponseDTO>> removeCloseFriend(
            @PathVariable("username") String username) {
        return RestResponseUtil.ok("Đã xóa khỏi danh sách bạn thân.",
                this.relationshipService.removeCloseFriend(username));
    }

    @PostMapping("/users/{username}/block")
    public ResponseEntity<RestResponseDTO<RelationshipActionResponseDTO>> block(
            @PathVariable("username") String username) {
        return RestResponseUtil.ok("Đã chặn tài khoản.", this.relationshipService.block(username));
    }

    @DeleteMapping("/users/{username}/block")
    public ResponseEntity<RestResponseDTO<RelationshipActionResponseDTO>> unblock(
            @PathVariable("username") String username) {
        return RestResponseUtil.ok("Đã bỏ chặn tài khoản.", this.relationshipService.unblock(username));
    }

    @GetMapping("/me/blocks")
    public ResponseEntity<RestResponseDTO<PageResponseDTO<BlockSummaryResponseDTO>>> getBlockedUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return RestResponseUtil.ok("Truy vấn danh sách đã chặn thành công.",
                this.relationshipService.getBlockedUsers(page, size));
    }
}
