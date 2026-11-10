package vn.tuhoc.ContentService.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.tuhoc.ContentService.domains.entities.PostEntity;
import vn.tuhoc.ContentService.domains.graphs.PostEntityGraph;
import vn.tuhoc.ContentService.domains.enums.PostModerationStatusEnum;
import vn.tuhoc.ContentService.domains.enums.PostSettingPrivacyEnum;
import vn.tuhoc.ContentService.domains.enums.PostStatusEnum;
import vn.tuhoc.ContentService.domains.enums.PostTypeEnum;

public interface PostRepository extends JpaRepository<PostEntity, Integer>, JpaSpecificationExecutor<PostEntity> {
    Boolean existsByPublicId(String publicId);

    long countByModerationStatus(PostModerationStatusEnum moderationStatus);

    @EntityGraph(value = PostEntityGraph.ADD_ALL)
    Optional<PostEntity> findById(Integer id);

    @EntityGraph(value = PostEntityGraph.ADD_ALL)
    Optional<PostEntity> findByPublicId(String publicId);

    @EntityGraph(value = PostEntityGraph.ADD_ALL)
    Page<PostEntity> findAll(Specification<PostEntity> specification, Pageable pageable);

    @Query(value = """
            select post.id
            from PostEntity post
            join post.setting setting
            where post.status = :status
                and post.moderationStatus = :moderationStatus
                and setting.privacy = :privacy
                and (:query is null or lower(post.caption) like concat('%', lower(:query), '%'))
                and (:reelsOnly is null
                    or (:reelsOnly = true and post.type = :reelType)
                    or (:reelsOnly = false and post.type <> :reelType))
            """, countQuery = """
            select count(post.id)
            from PostEntity post
            join post.setting setting
            where post.status = :status
                and post.moderationStatus = :moderationStatus
                and setting.privacy = :privacy
                and (:query is null or lower(post.caption) like concat('%', lower(:query), '%'))
                and (:reelsOnly is null
                    or (:reelsOnly = true and post.type = :reelType)
                    or (:reelsOnly = false and post.type <> :reelType))
            """)
    Page<Integer> findExplorePostIds(
            @Param("status") PostStatusEnum status,
            @Param("moderationStatus") PostModerationStatusEnum moderationStatus,
            @Param("privacy") PostSettingPrivacyEnum privacy,
            @Param("query") String query,
            @Param("reelsOnly") Boolean reelsOnly,
            @Param("reelType") PostTypeEnum reelType,
            Pageable pageable);

    @EntityGraph(value = PostEntityGraph.ADD_ALL)
    List<PostEntity> findAllByIdIn(List<Integer> ids);

}
