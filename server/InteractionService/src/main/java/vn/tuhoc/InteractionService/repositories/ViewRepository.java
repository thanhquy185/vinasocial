package vn.tuhoc.InteractionService.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.tuhoc.InteractionService.domains.entities.ViewEntity;
import vn.tuhoc.InteractionService.domains.enums.ViewTargetTypeEnum;

public interface ViewRepository extends MongoRepository<ViewEntity, Integer> {
    Page<ViewEntity> findAllByTargetTypeAndTargetId(
            ViewTargetTypeEnum targetType, Integer targetId, Pageable pageable);

    long countByTargetTypeAndTargetId(ViewTargetTypeEnum targetType, Integer targetId);

    List<ViewEntity> findAllByUserIdAndTargetTypeAndTargetIdIn(
            Integer userId, ViewTargetTypeEnum targetType, List<Integer> targetIds);
}
