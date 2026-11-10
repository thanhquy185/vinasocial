package vn.tuhoc.UserService.domains.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.converters.UserRoleConverter;
import vn.tuhoc.UserService.domains.converters.UserStatusConverter;
import vn.tuhoc.UserService.domains.enums.UserRoleEnum;
import vn.tuhoc.UserService.domains.enums.UserStatusEnum;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@NamedEntityGraph(name = "UserEntity.addUsernamesAndProfile", attributeNodes = {
        @NamedAttributeNode("usernames"),
        @NamedAttributeNode(value = "profile", subgraph = "profileSubgraph")
}, subgraphs = {
        @NamedSubgraph(name = "profileSubgraph", attributeNodes = {
                @NamedAttributeNode("stats"),
                @NamedAttributeNode("notificationSetting"),
                @NamedAttributeNode("privacySetting"),
        })
})
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(columnDefinition = "VARCHAR(10)", unique = true, nullable = false)
    String publicId;

    @Column(columnDefinition = "VARCHAR(19)", nullable = false)
    String createdAt;

    @Column(columnDefinition = "VARCHAR(19)", nullable = true)
    String lastLoginAt;

    @Column(columnDefinition = "VARCHAR(6)", nullable = false)
    @Convert(converter = UserRoleConverter.class)
    UserRoleEnum role;

    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    String password;

    @Column(columnDefinition = "VARCHAR(8)", nullable = false)
    @Convert(converter = UserStatusConverter.class)
    UserStatusEnum status;

    @Builder.Default
    @Column(name = "two_factor_enabled", columnDefinition = "BIT(1)", nullable = false)
    Boolean twoFactorEnabled = false;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    ProfileEntity profile;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    List<UsernameEntity> usernames;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    List<AuthSessionEntity> authSessions;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    List<AuthOtpEntity> authOtps;
}
