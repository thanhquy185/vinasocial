package vn.tuhoc.RelationshipService.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import vn.tuhoc.RelationshipService.domains.entities.BlockEntity;

public interface BlockRepository extends JpaRepository<BlockEntity, Integer> {
    boolean existsByBlockerIdAndBlockedId(Integer blockerId, Integer blockedId);

    Optional<BlockEntity> findByBlockerIdAndBlockedId(Integer blockerId, Integer blockedId);

    Page<BlockEntity> findAllByBlockerId(Integer blockerId, Pageable pageable);

    void deleteByBlockerIdAndBlockedId(Integer blockerId, Integer blockedId);
}
