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
@Table(name = "profile_stats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileStatsEntity {
    @Id
    @Column(columnDefinition = "INT")
    Integer profileId;

    @Column(columnDefinition = "INT", nullable = false)
    Integer mediaCount;

    @Column(columnDefinition = "INT", nullable = false)
    Integer friendCount;

    @Column(columnDefinition = "INT", nullable = false)
    Integer bestFriendCount;

    @Column(columnDefinition = "INT", nullable = false)
    Integer followerCount;

    @Column(columnDefinition = "INT", nullable = false)
    Integer followingCount;

    @Column(columnDefinition = "INT", nullable = false)
    Integer updatedCount;

    @OneToOne
    @MapsId
    @JoinColumn(name = "profile_id")
    ProfileEntity profile;
}
