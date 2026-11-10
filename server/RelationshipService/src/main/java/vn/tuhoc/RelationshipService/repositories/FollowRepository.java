package vn.tuhoc.RelationshipService.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import vn.tuhoc.RelationshipService.domains.entities.FollowEntity;

public interface FollowRepository extends JpaRepository<FollowEntity, Integer> {
    boolean existsByFollowerIdAndFollowingId(Integer followerId, Integer followingId);

    Optional<FollowEntity> findByFollowerIdAndFollowingId(Integer followerId, Integer followingId);

    Page<FollowEntity> findAllByFollowerId(Integer followerId, Pageable pageable);

    Page<FollowEntity> findAllByFollowingId(Integer followingId, Pageable pageable);

    List<FollowEntity> findAllByFollowerIdAndFollowingIdIn(Integer followerId, List<Integer> followingIds);

    List<FollowEntity> findAllByFollowerIdInAndFollowingId(List<Integer> followerIds, Integer followingId);

    long countByFollowerId(Integer followerId);

    long countByFollowingId(Integer followingId);

    void deleteByFollowerIdAndFollowingId(Integer followerId, Integer followingId);
}
