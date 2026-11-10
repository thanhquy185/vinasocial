package vn.tuhoc.RelationshipService.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import vn.tuhoc.RelationshipService.domains.entities.CloseFriendEntity;

public interface CloseFriendRepository extends JpaRepository<CloseFriendEntity, Integer> {
    boolean existsByOwnerUserIdAndMemberUserId(Integer ownerUserId, Integer memberUserId);

    Page<CloseFriendEntity> findAllByOwnerUserId(Integer ownerUserId, Pageable pageable);

    List<CloseFriendEntity> findAllByOwnerUserIdInAndMemberUserId(List<Integer> ownerUserIds, Integer memberUserId);

    void deleteByOwnerUserIdAndMemberUserId(Integer ownerUserId, Integer memberUserId);
}
