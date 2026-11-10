package vn.tuhoc.RelationshipService.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import vn.tuhoc.RelationshipService.domains.entities.FollowRequestEntity;
import vn.tuhoc.RelationshipService.domains.enums.FollowRequestStatusEnum;

public interface FollowRequestRepository extends JpaRepository<FollowRequestEntity, Integer> {
    Optional<FollowRequestEntity> findBySenderIdAndReceiverId(Integer senderId, Integer receiverId);

    Optional<FollowRequestEntity> findByIdAndReceiverId(Integer id, Integer receiverId);

    Optional<FollowRequestEntity> findByIdAndSenderId(Integer id, Integer senderId);

    Page<FollowRequestEntity> findAllByReceiverIdAndStatus(
            Integer receiverId,
            FollowRequestStatusEnum status,
            Pageable pageable);
}
