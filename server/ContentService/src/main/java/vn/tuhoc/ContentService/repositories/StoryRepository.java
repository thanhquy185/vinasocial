package vn.tuhoc.ContentService.repositories;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
import vn.tuhoc.ContentService.domains.entities.StoryEntity;
import vn.tuhoc.ContentService.domains.enums.StoryModerationStatusEnum;
import vn.tuhoc.ContentService.domains.enums.StoryStatusEnum;
import vn.tuhoc.ContentService.domains.graphs.StoryEntityGraph;

public interface StoryRepository extends JpaRepository<StoryEntity, Integer>, JpaSpecificationExecutor<StoryEntity> {
    @EntityGraph(value = StoryEntityGraph.ADD_ALL)
    Optional<StoryEntity> findById(Integer id);

    @EntityGraph(value = StoryEntityGraph.ADD_ALL)
    Optional<StoryEntity> findByPublicId(String publicId);

    @EntityGraph(value = StoryEntityGraph.ADD_ALL)
    Page<StoryEntity> findAll(Specification<StoryEntity> specification, Pageable pageable);

    @EntityGraph(value = StoryEntityGraph.ADD_ALL)
    Page<StoryEntity> findAllByStatusAndModerationStatusAndExpiredAtGreaterThanOrderByCreatedAtDesc(
            StoryStatusEnum status,
            StoryModerationStatusEnum moderationStatus,
            String currentDatetime,
            Pageable pageable);

    @EntityGraph(value = StoryEntityGraph.ADD_ALL)
    Page<StoryEntity> findAllByUserIdAndExpiredAtGreaterThanOrderByCreatedAtDesc(
            Integer userId,
            String currentDatetime,
            Pageable pageable);

    @EntityGraph(value = StoryEntityGraph.ADD_ALL)
    List<StoryEntity> findAllByUserIdAndStatusNotAndModerationStatusNotOrderByCreatedAtAsc(
            Integer userId,
            StoryStatusEnum excludedStatus,
            StoryModerationStatusEnum excludedModerationStatus);

    boolean existsByPublicId(String publicId);

    long countByModerationStatus(StoryModerationStatusEnum moderationStatus);
}
