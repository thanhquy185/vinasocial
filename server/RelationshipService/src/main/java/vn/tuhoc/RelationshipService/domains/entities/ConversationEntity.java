package vn.tuhoc.RelationshipService.domains.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import vn.tuhoc.RelationshipService.domains.enums.ConversationTypeEnum;

@Entity
@Table(name = "conversations", uniqueConstraints = @UniqueConstraint(
        name = "uk_conversations_direct_pair",
        columnNames = { "direct_user_low_id", "direct_user_high_id" }))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "created_by_id", nullable = false)
    Integer createdById;

    @Column(name = "direct_user_low_id")
    Integer directUserLowId;

    @Column(name = "direct_user_high_id")
    Integer directUserHighId;

    @Column(name = "last_message_id")
    Integer lastMessageId;

    @Column(length = 100)
    String name;

    @Column(name = "created_at", columnDefinition = "VARCHAR(19)", nullable = false)
    String createdAt;

    @Column(name = "updated_at", columnDefinition = "VARCHAR(19)", nullable = false)
    String updatedAt;

    @Column(name = "last_activity_at", columnDefinition = "VARCHAR(19)", nullable = false)
    String lastActivityAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "`type`", columnDefinition = "VARCHAR(8)", nullable = false)
    ConversationTypeEnum type;
}
