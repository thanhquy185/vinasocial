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
import vn.tuhoc.RelationshipService.domains.enums.ConversationRequestStatusEnum;

@Entity
@Table(name = "conversation_requests", uniqueConstraints = @UniqueConstraint(
        name = "uk_conversation_requests_conversation", columnNames = "conversation_id"))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationRequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "conversation_id", nullable = false)
    Integer conversationId;

    @Column(name = "sender_id", nullable = false)
    Integer senderId;

    @Column(name = "receiver_id", nullable = false)
    Integer receiverId;

    @Column(name = "created_at", columnDefinition = "VARCHAR(19)", nullable = false)
    String createdAt;

    @Column(name = "responded_at", columnDefinition = "VARCHAR(19)")
    String respondedAt;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10)", nullable = false)
    ConversationRequestStatusEnum status;
}
