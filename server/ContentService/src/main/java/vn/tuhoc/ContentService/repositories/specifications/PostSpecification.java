package vn.tuhoc.ContentService.repositories.specifications;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import vn.tuhoc.ContentService.domains.entities.PostEntity;
import vn.tuhoc.ContentService.domains.entities.PostEntity_;
import vn.tuhoc.ContentService.domains.enums.PostModerationStatusEnum;
import vn.tuhoc.ContentService.domains.enums.PostStatusEnum;
import vn.tuhoc.ContentService.domains.enums.PostTypeEnum;
import vn.tuhoc.ContentService.repositories.criteria.PostCriteria;
import vn.tuhoc.core.utils.ValidationUtil;

public class PostSpecification {

    public static Specification<PostEntity> filterPosts(PostCriteria postCriteria) {
        if (ValidationUtil.isNull(postCriteria.getId())
                && ValidationUtil.isNull(postCriteria.getPublicId())
                && ValidationUtil.isNull(postCriteria.getUserId())
                && ValidationUtil.isNull(postCriteria.getCaption())
                && ValidationUtil.isNull(postCriteria.getCreatedAtStart())
                && ValidationUtil.isNull(postCriteria.getCreatedAtEnd())
                && ValidationUtil.isNull(postCriteria.getType())
                && ValidationUtil.isNull(postCriteria.getModerationStatus())
                && ValidationUtil.isNull(postCriteria.getStatus())) {
            return null;
        }

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Id
            if (ValidationUtil.nonNull(postCriteria.getId())) {
                postCriteria.getId()
                        .filter(ValidationUtil::isNumeric)
                        .ifPresent(id -> predicates.add(
                                criteriaBuilder.equal(
                                        root.get(PostEntity_.ID),
                                        Long.valueOf(id))));
            }

            // Public Id
            if (ValidationUtil.nonNull(postCriteria.getPublicId())) {
                postCriteria.getPublicId()
                        .filter(ValidationUtil::hasText)
                        .ifPresent(publicId -> predicates.add(
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(
                                                root.get(PostEntity_.PUBLIC_ID)),
                                        publicId.toLowerCase() + "%")));
            }

            // Author
            if (ValidationUtil.nonNull(postCriteria.getUserId())) {
                postCriteria.getUserId()
                        .filter(ValidationUtil::isNumeric)
                        .ifPresent(userId -> predicates.add(
                                criteriaBuilder.equal(
                                        root.get(PostEntity_.USER_ID),
                                        Integer.valueOf(userId))));
            }

            // Caption
            if (ValidationUtil.nonNull(postCriteria.getCaption())) {
                postCriteria.getCaption()
                        .filter(ValidationUtil::hasText)
                        .ifPresent(caption -> predicates.add(
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(root.get(PostEntity_.CAPTION)),
                                        "%" + caption.toLowerCase() + "%")));
            }

            // Created At Start
            if (ValidationUtil.nonNull(postCriteria.getCreatedAtStart())) {
                postCriteria.getCreatedAtStart()
                        .filter(ValidationUtil::hasText)
                        .ifPresent(createdAtStart -> predicates.add(
                                criteriaBuilder.greaterThanOrEqualTo(
                                        root.get(PostEntity_.CREATED_AT),
                                        createdAtStart)));
            }

            // Created At End
            if (ValidationUtil.nonNull(postCriteria.getCreatedAtEnd())) {
                postCriteria.getCreatedAtEnd()
                        .filter(ValidationUtil::hasText)
                        .ifPresent(createdAtEnd -> predicates.add(
                                criteriaBuilder.lessThanOrEqualTo(
                                        root.get(PostEntity_.CREATED_AT),
                                        createdAtEnd)));
            }

            // Type
            if (ValidationUtil.nonNull(postCriteria.getType())) {
                postCriteria.getType()
                        .filter(ValidationUtil::hasText)
                        .ifPresent(type -> {
                            PostTypeEnum typeEnum = PostTypeEnum.fromDescription(type);

                            predicates.add(
                                    criteriaBuilder.equal(
                                            root.get(PostEntity_.TYPE),
                                            typeEnum.getValue()));
                        });
            }

            // Moderation status
            if (ValidationUtil.nonNull(postCriteria.getModerationStatus())) {
                postCriteria.getModerationStatus()
                        .filter(ValidationUtil::hasText)
                        .ifPresent(moderationStatus -> {
                            var moderationStatusEnum = PostModerationStatusEnum.fromDescription(moderationStatus);

                            predicates.add(
                                    criteriaBuilder.equal(
                                            root.get(PostEntity_.MODERATION_STATUS),
                                            moderationStatusEnum.getValue()));
                        });
            }

            // Visibility status
            if (ValidationUtil.nonNull(postCriteria.getStatus())) {
                postCriteria.getStatus()
                        .filter(ValidationUtil::hasText)
                        .ifPresent(status -> {
                            PostStatusEnum statusEnum = PostStatusEnum.fromDescription(status);

                            predicates.add(
                                    criteriaBuilder.equal(
                                            root.get(PostEntity_.STATUS),
                                            statusEnum.getValue()));
                        });
            }

            return criteriaBuilder.and(
                    predicates.toArray(new Predicate[0]));
        };
    }
}
