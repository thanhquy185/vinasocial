package vn.tuhoc.InteractionService.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.tuhoc.InteractionService.domains.entities.ShareEntity;
import vn.tuhoc.InteractionService.domains.enums.ShareTargetTypeEnum;

public interface ShareRepository extends MongoRepository<ShareEntity, Integer> {
    Page<ShareEntity> findAllByTargetTypeAndTargetId(
            ShareTargetTypeEnum targetType, Integer targetId, Pageable pageable);

    long countByTargetTypeAndTargetId(ShareTargetTypeEnum targetType, Integer targetId);
}
