package vn.tuhoc.InteractionService.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.tuhoc.InteractionService.domains.entities.CommentEntity;
import vn.tuhoc.InteractionService.domains.enums.CommentStatusEnum;
import vn.tuhoc.InteractionService.domains.enums.CommentTargetTypeEnum;

public interface CommentRepository extends MongoRepository<CommentEntity, Integer>, CommentRepositoryCustom {
    Page<CommentEntity> findAllByTargetTypeAndTargetIdAndStatus(
            CommentTargetTypeEnum targetType, Integer targetId, CommentStatusEnum status, Pageable pageable);

    Optional<CommentEntity> findByIdAndStatus(Integer id, CommentStatusEnum status);

    long countByTargetTypeAndTargetIdAndStatus(
            CommentTargetTypeEnum targetType, Integer targetId, CommentStatusEnum status);
}
