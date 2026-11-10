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
import vn.tuhoc.RelationshipService.domains.enums.ConversationParticipantRoleEnum;

@Entity
@Table(name = "conversation_participants", uniqueConstraints = @UniqueConstraint(
        name = "uk_conversation_participants_pair",
        columnNames = { "conversation_id", "user_id" }))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationParticipantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "conversation_id", nullable = false)
    Integer conversationId;

    @Column(name = "user_id", nullable = false)
    Integer userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "`role`", columnDefinition = "VARCHAR(6)", nullable = false)
    ConversationParticipantRoleEnum role;

    @Column(name = "joined_at", columnDefinition = "VARCHAR(19)", nullable = false)
    String joinedAt;

    @Column(name = "left_at", columnDefinition = "VARCHAR(19)")
    String leftAt;

    @Column(name = "last_read_message_id")
    Integer lastReadMessageId;
}
