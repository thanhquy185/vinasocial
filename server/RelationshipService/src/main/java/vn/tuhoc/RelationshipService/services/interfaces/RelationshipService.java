package vn.tuhoc.RelationshipService.services.interfaces;

import java.util.List;

import vn.tuhoc.RelationshipService.dtos.responses.CloseFriendSummaryResponseDTO;
import vn.tuhoc.RelationshipService.dtos.responses.BlockSummaryResponseDTO;
import vn.tuhoc.RelationshipService.dtos.responses.FollowRequestSummaryResponseDTO;
import vn.tuhoc.RelationshipService.dtos.responses.FollowSummaryResponseDTO;
import vn.tuhoc.RelationshipService.dtos.responses.RelationshipActionResponseDTO;
import vn.tuhoc.RelationshipService.dtos.responses.RelationshipStatusResponseDTO;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;

public interface RelationshipService {
    RelationshipActionResponseDTO follow(String username);

    RelationshipActionResponseDTO unfollowOrCancel(String username);

    PageResponseDTO<FollowSummaryResponseDTO> getFollowers(int page, int size);

    PageResponseDTO<FollowSummaryResponseDTO> getFollowing(int page, int size);

    PageResponseDTO<FollowRequestSummaryResponseDTO> getIncomingFollowRequests(int page, int size);

    RelationshipActionResponseDTO acceptFollowRequest(Integer requestId);

    RelationshipActionResponseDTO rejectFollowRequest(Integer requestId);

    RelationshipActionResponseDTO cancelFollowRequest(Integer requestId);

    RelationshipStatusResponseDTO getRelationshipStatus(String username);

    List<Integer> getStoryAudience(String visibility, List<Integer> ownerUserIds);

    PageResponseDTO<CloseFriendSummaryResponseDTO> getCloseFriends(int page, int size);

    RelationshipActionResponseDTO addCloseFriend(String username);

    RelationshipActionResponseDTO removeCloseFriend(String username);

    RelationshipActionResponseDTO block(String username);

    RelationshipActionResponseDTO unblock(String username);

    PageResponseDTO<BlockSummaryResponseDTO> getBlockedUsers(int page, int size);
}
