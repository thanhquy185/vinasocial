package vn.tuhoc.RelationshipService.domains.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "follows", uniqueConstraints = @UniqueConstraint(name = "uk_follows_pair", columnNames = { "follower_id", "following_id" }))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FollowEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "follower_id", nullable = false)
    Integer followerId;

    @Column(name = "following_id", nullable = false)
    Integer followingId;

    @Column(name = "created_at", columnDefinition = "VARCHAR(19)", nullable = false)
    String createdAt;
}
