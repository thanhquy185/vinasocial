package vn.tuhoc.ContentService.repositories.specifications;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import vn.tuhoc.ContentService.domains.entities.StoryEntity;
import vn.tuhoc.ContentService.domains.entities.StoryEntity_;
import vn.tuhoc.ContentService.domains.entities.StorySettingEntity;
import vn.tuhoc.ContentService.domains.entities.StorySettingEntity_;
import vn.tuhoc.ContentService.domains.enums.StoryModerationStatusEnum;
import vn.tuhoc.ContentService.domains.enums.StoryStatusEnum;
import vn.tuhoc.ContentService.domains.enums.StoryVisibilityEnum;
import vn.tuhoc.ContentService.repositories.criteria.StoryCriteria;
import vn.tuhoc.core.utils.ValidationUtil;

public class StorySpecification {

    public static Specification<StoryEntity> filterStories(StoryCriteria storyCriteria) {
        if (ValidationUtil.isNull(storyCriteria.getId())
                && ValidationUtil.isNull(storyCriteria.getPublicId())
                && ValidationUtil.isNull(storyCriteria.getUserId())
                && ValidationUtil.isNull(storyCriteria.getCreatedAtStart())
                && ValidationUtil.isNull(storyCriteria.getCreatedAtEnd())
                && ValidationUtil.isNull(storyCriteria.getExpiredAtStart())
                && ValidationUtil.isNull(storyCriteria.getExpiredAtEnd())
                && ValidationUtil.isNull(storyCriteria.getStatus())
                && ValidationUtil.isNull(storyCriteria.getModerationStatus())
                && ValidationUtil.isNull(storyCriteria.getVisibility())) {
            return null;
        }

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Id
            if (ValidationUtil.nonNull(storyCriteria.getId())) {
                storyCriteria.getId()
                        .filter(ValidationUtil::isNumeric)
                        .ifPresent(id -> predicates.add(
                                criteriaBuilder.equal(
                                        root.get(StoryEntity_.ID),
                                        Integer.valueOf(id))));
            }

            // Public Id
            if (ValidationUtil.nonNull(storyCriteria.getPublicId())) {
                storyCriteria.getPublicId()
                        .filter(ValidationUtil::hasText)
                        .ifPresent(publicId -> predicates.add(
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(root.get(StoryEntity_.PUBLIC_ID)),
                                        publicId.toLowerCase() + "%")));
            }

            // Author
            if (ValidationUtil.nonNull(storyCriteria.getUserId())) {
                storyCriteria.getUserId()
                        .filter(ValidationUtil::isNumeric)
                        .ifPresent(userId -> predicates.add(
                                criteriaBuilder.equal(
                                        root.get(StoryEntity_.USER_ID),
                                        Integer.valueOf(userId))));
            }

            // Created At Start
            if (ValidationUtil.nonNull(storyCriteria.getCreatedAtStart())) {
                storyCriteria.getCreatedAtStart()
                        .filter(ValidationUtil::hasText)
                        .ifPresent(createdAtStart -> predicates.add(
                                criteriaBuilder.greaterThanOrEqualTo(
                                        root.get(StoryEntity_.CREATED_AT),
                                        createdAtStart)));
            }

            // Created At End
            if (ValidationUtil.nonNull(storyCriteria.getCreatedAtEnd())) {
                storyCriteria.getCreatedAtEnd()
                        .filter(ValidationUtil::hasText)
                        .ifPresent(createdAtEnd -> predicates.add(
                                criteriaBuilder.lessThanOrEqualTo(
                                        root.get(StoryEntity_.CREATED_AT),
                                        createdAtEnd)));
            }

            // Expired At Start
            if (ValidationUtil.nonNull(storyCriteria.getExpiredAtStart())) {
                storyCriteria.getExpiredAtStart()
                        .filter(ValidationUtil::hasText)
                        .ifPresent(expiredAtStart -> predicates.add(
                                criteriaBuilder.greaterThanOrEqualTo(
                                        root.get(StoryEntity_.EXPIRED_AT),
                                        expiredAtStart)));
            }

            // Expired At End
            if (ValidationUtil.nonNull(storyCriteria.getExpiredAtEnd())) {
                storyCriteria.getExpiredAtEnd()
                        .filter(ValidationUtil::hasText)
                        .ifPresent(expiredAtEnd -> predicates.add(
                                criteriaBuilder.lessThanOrEqualTo(
                                        root.get(StoryEntity_.EXPIRED_AT),
                                        expiredAtEnd)));
            }

            // Status
            if (ValidationUtil.nonNull(storyCriteria.getStatus())) {
                storyCriteria.getStatus()
                        .filter(ValidationUtil::hasText)
                        .ifPresent(status -> {
                            StoryStatusEnum statusEnum = StoryStatusEnum.fromDescription(status);

                            predicates.add(criteriaBuilder.equal(
                                    root.get(StoryEntity_.STATUS),
                                    statusEnum.getValue()));
                        });
            }

            // Moderation status
            if (ValidationUtil.nonNull(storyCriteria.getModerationStatus())) {
                storyCriteria.getModerationStatus()
                        .filter(ValidationUtil::hasText)
                        .ifPresent(moderationStatus -> {
                            StoryModerationStatusEnum moderationStatusEnum =
                                    StoryModerationStatusEnum.fromDescription(moderationStatus);

                            predicates.add(criteriaBuilder.equal(
                                    root.get(StoryEntity_.MODERATION_STATUS),
                                    moderationStatusEnum.getValue()));
                        });
            }

            // Visibility
            if (ValidationUtil.nonNull(storyCriteria.getVisibility())) {
                storyCriteria.getVisibility()
                        .filter(ValidationUtil::hasText)
                        .ifPresent(visibility -> {
                            StoryVisibilityEnum visibilityEnum = StoryVisibilityEnum.fromDescription(visibility);
                            Join<StoryEntity, StorySettingEntity> setting = root.join(
                                    StoryEntity_.SETTING,
                                    JoinType.INNER);

                            predicates.add(criteriaBuilder.equal(
                                    setting.get(StorySettingEntity_.VISIBILITY),
                                    visibilityEnum.getValue()));
                        });
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
