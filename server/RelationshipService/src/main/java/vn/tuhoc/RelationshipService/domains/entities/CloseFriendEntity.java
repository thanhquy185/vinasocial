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
@Table(name = "close_friends", uniqueConstraints = @UniqueConstraint(name = "uk_close_friends_pair", columnNames = { "owner_user_id", "member_user_id" }))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CloseFriendEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "owner_user_id", nullable = false)
    Integer ownerUserId;

    @Column(name = "member_user_id", nullable = false)
    Integer memberUserId;

    @Column(name = "created_at", columnDefinition = "VARCHAR(19)", nullable = false)
    String createdAt;
}
