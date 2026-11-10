package vn.tuhoc.UserService.domains.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "profile_notification_settings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileNotificationSettingEntity {
    @Id
    @Column(columnDefinition = "INT")
    Integer profileId;

    @Column(columnDefinition = "BIT", nullable = false)
    Boolean postEnabled;

    @Column(columnDefinition = "BIT", nullable = false)
    Boolean storyEnabled;

    @Column(columnDefinition = "BIT", nullable = false)
    Boolean collaboratorEnabled;

    @Column(columnDefinition = "BIT", nullable = false)
    Boolean tagEnabled;

    @Column(columnDefinition = "BIT", nullable = false)
    Boolean messageEnabled;

    @Column(columnDefinition = "BIT", nullable = false)
    Boolean followEnabled;

    @Column(columnDefinition = "BIT", nullable = false)
    Boolean friendEnabled;

    @Column(columnDefinition = "BIT", nullable = false)
    Boolean bestFriendEnabled;

    @Column(columnDefinition = "BIT", nullable = false)
    Boolean pageFollowingEnabled;

    @Column(columnDefinition = "BIT", nullable = false)
    Boolean replyCommentEnabled;

    @OneToOne
    @MapsId
    @JoinColumn(name = "profile_id")
    ProfileEntity profile;
}
