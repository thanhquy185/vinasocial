package vn.tuhoc.UserService.repositories.specifications;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import vn.tuhoc.UserService.domains.entities.UserEntity;
import vn.tuhoc.UserService.domains.entities.UserEntity_;
import vn.tuhoc.UserService.domains.entities.UsernameEntity;
import vn.tuhoc.UserService.domains.entities.UsernameEntity_;
import vn.tuhoc.UserService.domains.enums.UserRoleEnum;
import vn.tuhoc.UserService.domains.enums.UserStatusEnum;
import vn.tuhoc.UserService.domains.enums.UsernameTypeEnum;
import vn.tuhoc.UserService.repositories.criteria.UserCriteria;
import vn.tuhoc.core.utils.ValidationUtil;

public class UserSpecification {
    public static Specification<UserEntity> filterUsers(UserCriteria userCriteria) {
        if (ValidationUtil.isNull(userCriteria.getId())
                && ValidationUtil.isNull(userCriteria.getPublicId())
                && ValidationUtil.isNull(userCriteria.getCreatedAtStart())
                && ValidationUtil.isNull(userCriteria.getCreatedAtEnd())
                && ValidationUtil.isNull(userCriteria.getLastLoginAtStart())
                && ValidationUtil.isNull(userCriteria.getLastLoginAtEnd())
                && ValidationUtil.isNull(userCriteria.getUsername())
                && ValidationUtil.isNull(userCriteria.getRole())
                && ValidationUtil.isNull(userCriteria.getStatus())) {
            return null;
        }

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Id
            if (ValidationUtil.nonNull(userCriteria.getId())) {
                userCriteria.getId()
                        .filter(ValidationUtil::isNumeric)
                        .ifPresent(id -> predicates.add(
                                criteriaBuilder.equal(root.get(UserEntity_.ID),
                                        Long.valueOf(id))));
            }
            // Public Id
            if (ValidationUtil.nonNull(userCriteria.getPublicId())) {
                userCriteria.getPublicId()
                        .filter(ValidationUtil::hasText)
                        .ifPresent(publicId -> predicates.add(
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(root.get(UserEntity_.PUBLIC_ID)),
                                        "%" + publicId.trim().toLowerCase() + "%")));
            }
            // Create At Start
            if (ValidationUtil.nonNull(userCriteria.getCreatedAtStart())) {
                userCriteria.getCreatedAtStart()
                        .filter(ValidationUtil::hasText)
                        .ifPresent(createAtStart -> predicates.add(
                                criteriaBuilder.greaterThanOrEqualTo(
                                        root.get(UserEntity_.CREATED_AT),
                                        createAtStart)));
            }
            // Create At End
            if (ValidationUtil.nonNull(userCriteria.getCreatedAtEnd())) {
                userCriteria.getCreatedAtEnd()
                        .filter(ValidationUtil::hasText)
                        .ifPresent(createAtEnd -> predicates.add(
                                criteriaBuilder.lessThanOrEqualTo(
                                        root.get(UserEntity_.CREATED_AT),
                                        createAtEnd)));
            }
            // Last Login Start
            if (ValidationUtil.nonNull(userCriteria.getLastLoginAtStart())) {
                userCriteria.getLastLoginAtStart()
                        .filter(ValidationUtil::hasText)
                        .ifPresent(lastLoginAtStart -> predicates.add(
                                criteriaBuilder.greaterThanOrEqualTo(
                                        root.get(UserEntity_.LAST_LOGIN_AT),
                                        lastLoginAtStart)));
            }
            // Last Login End
            if (ValidationUtil.nonNull(userCriteria.getLastLoginAtEnd())) {
                userCriteria.getLastLoginAtEnd()
                        .filter(ValidationUtil::hasText)
                        .ifPresent(lastLoginAtEnd -> predicates.add(
                                criteriaBuilder.lessThanOrEqualTo(
                                        root.get(UserEntity_.LAST_LOGIN_AT),
                                        lastLoginAtEnd)));
            }
            // Username
            if (ValidationUtil.nonNull(userCriteria.getUsername())) {
                userCriteria.getUsername()
                        .filter(username -> !username.isBlank())
                        .ifPresent(username -> {
                            Subquery<Integer> subquery = query.subquery(Integer.class);
                            Root<UsernameEntity> usernameRoot = subquery
                                    .from(UsernameEntity.class);

                            Predicate usernameMatch = criteriaBuilder.like(
                                    criteriaBuilder.lower(usernameRoot.get(UsernameEntity_.VALUE)),
                                    "%" + username.trim().toLowerCase() + "%");
                            Predicate usernameTypeMatch = criteriaBuilder.equal(
                                    usernameRoot.get(UsernameEntity_.TYPE),
                                    UsernameTypeEnum.TEXT);

                            Predicate userMatch = criteriaBuilder.equal(
                                    usernameRoot.get(UsernameEntity_.USER)
                                            .get(UserEntity_.ID),
                                    root.get(UserEntity_.ID));

                            subquery.select(
                                    usernameRoot.get(UsernameEntity_.USER)
                                            .get(UserEntity_.ID))
                                    .where(criteriaBuilder.and(
                                            usernameMatch,
                                            usernameTypeMatch,
                                            userMatch));

                            predicates.add(criteriaBuilder.exists(subquery));
                        });
            }
            // Role
            if (ValidationUtil.nonNull(userCriteria.getRole())) {
                userCriteria.getRole()
                        .filter(ValidationUtil::hasText)
                        .ifPresent(role -> {
                            UserRoleEnum roleEnum = UserRoleEnum.fromDescription(role);
                            predicates.add(criteriaBuilder.equal(
                                    root.get(UserEntity_.ROLE),
                                    roleEnum.getValue()));
                        });
            }
            // Status
            if (ValidationUtil.nonNull(userCriteria.getStatus())) {
                userCriteria.getStatus()
                        .filter(ValidationUtil::hasText)
                        .ifPresent(status -> {
                            UserStatusEnum statusEnum = UserStatusEnum.fromDescription(status);
                            predicates.add(criteriaBuilder.equal(
                                    root.get(UserEntity_.STATUS),
                                    statusEnum.getValue()));
                        });
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
