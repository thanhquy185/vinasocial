package vn.tuhoc.UserService.repositories;

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

import vn.tuhoc.UserService.domains.entities.UserEntity;
import vn.tuhoc.UserService.domains.enums.ProfilePrivacySettingPermissionEnum;
import vn.tuhoc.UserService.domains.graphs.UserEntityGraph;
import vn.tuhoc.core.dtos.clients.UserInfoResClientDTO;

public interface UserRepository extends JpaRepository<UserEntity, Integer>, JpaSpecificationExecutor<UserEntity> {
        Boolean existsByPublicId(String publicId);

        @EntityGraph(value = UserEntityGraph.ADD_USERNAMES_AND_PROFILE)
        @Query("""
                                select distinct u
                                from UserEntity u
                                where u.id = :id
                        """)
        Optional<UserEntity> findById(@Param("id") Integer id);

        @EntityGraph(value = UserEntityGraph.ADD_USERNAMES_AND_PROFILE)
        @Query("""
                                select distinct u
                                from UserEntity u
                                where u.publicId = :publicId
                        """)
        Optional<UserEntity> findByPublicId(@Param("publicId") String publicId);

        @EntityGraph(value = UserEntityGraph.ADD_USERNAMES_AND_PROFILE)
        @Query("""
                            select distinct u
                            from UserEntity u
                            join fetch u.usernames un
                            where un.value = :username
                        """)
        Optional<UserEntity> findByUsername(@Param("username") String username);

        @EntityGraph(value = UserEntityGraph.ADD_USERNAMES_AND_PROFILE)
        @Query("""
                            select distinct u
                            from UserEntity u
                            join fetch u.usernames un
                            where un.value = :username
                                and u.password = :password
                        """)
        Optional<UserEntity> findByUsernameAndPassword(
                        @Param("username") String username,
                        @Param("password") String password);

        @EntityGraph(value = UserEntityGraph.ADD_USERNAMES_AND_PROFILE)
        @Query("""
                            select new vn.tuhoc.core.dtos.clients.UserInfoResClientDTO(
                                u.id,
                                u.publicId,
                                p.avatar,
                                p.username,
                                p.alias,
                                u.role,
                                case when u.id = :viewerId then true
                                    when privacy.isPrivate = false
                                        and privacy.showProfilePermission = :everyone
                                        and privacy.showPostsPermission = :everyone
                                    then true else false end
                            )
                            from UserEntity u
                            left join u.profile p
                            left join p.privacySetting privacy
                            where u.id in :ids
                        """)
        List<UserInfoResClientDTO> findUserInfoByIds(
                        @Param("ids") List<Integer> ids,
                        @Param("viewerId") Integer viewerId,
                        @Param("everyone") ProfilePrivacySettingPermissionEnum everyone);

        @EntityGraph(value = UserEntityGraph.ADD_USERNAMES_AND_PROFILE)
        Page<UserEntity> findAll(Specification<UserEntity> specification, Pageable pageable);
}
