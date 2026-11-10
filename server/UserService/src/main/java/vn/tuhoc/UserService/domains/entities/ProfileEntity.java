package vn.tuhoc.UserService.domains.entities;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.converters.ProfileGenderConverter;
import vn.tuhoc.UserService.domains.enums.ProfileGenderEnum;

@Entity
@Table(name = "profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    String firstName;

    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    String lastName;

    @Column(columnDefinition = "VARCHAR(10)", nullable = false)
    String birthdate;

    @Column(columnDefinition = "VARCHAR(6)", nullable = false)
    @Convert(converter = ProfileGenderConverter.class)
    ProfileGenderEnum gender;

    @Column(columnDefinition = "VARCHAR(11)", nullable = true, unique = true)
    String phone;

    @Column(columnDefinition = "VARCHAR(255)", nullable = true, unique = true)
    String email;

    @Column(columnDefinition = "VARCHAR(24)", nullable = true)
    String avatar;

    @Column(columnDefinition = "VARCHAR(100)", nullable = false, unique = true)
    String username;

    @Column(columnDefinition = "NVARCHAR(100)", nullable = true)
    String alias;

    @Column(columnDefinition = "TEXT", nullable = true)
    String bio;

    @Column(columnDefinition = "TEXT", nullable = true)
    String note;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity user;

    @OneToOne(mappedBy = "profile", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    ProfileStatsEntity stats;

    @OneToOne(mappedBy = "profile", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    ProfileNotificationSettingEntity notificationSetting;

    @OneToOne(mappedBy = "profile", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    ProfilePrivacySettingEntity privacySetting;

    @OneToMany(mappedBy = "profile", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    List<ProfileLinkEntity> links;
}
