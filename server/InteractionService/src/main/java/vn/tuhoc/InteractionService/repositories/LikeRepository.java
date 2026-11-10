package vn.tuhoc.InteractionService.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.tuhoc.InteractionService.domains.entities.LikeEntity;
import vn.tuhoc.InteractionService.domains.enums.LikeTargetTypeEnum;

public interface LikeRepository extends MongoRepository<LikeEntity, Integer> {
    Optional<LikeEntity> findByUserIdAndTargetTypeAndTargetId(
            Integer userId, LikeTargetTypeEnum targetType, Integer targetId);

    boolean existsByUserIdAndTargetTypeAndTargetId(
            Integer userId, LikeTargetTypeEnum targetType, Integer targetId);

    Page<LikeEntity> findAllByTargetTypeAndTargetId(
            LikeTargetTypeEnum targetType, Integer targetId, Pageable pageable);

    long countByTargetTypeAndTargetId(LikeTargetTypeEnum targetType, Integer targetId);

    void deleteByUserIdAndTargetTypeAndTargetId(
            Integer userId, LikeTargetTypeEnum targetType, Integer targetId);
}
