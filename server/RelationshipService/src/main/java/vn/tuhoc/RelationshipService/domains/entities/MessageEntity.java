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
import vn.tuhoc.RelationshipService.domains.enums.MessageStatusEnum;
import vn.tuhoc.RelationshipService.domains.enums.MessageTypeEnum;

@Entity
@Table(name = "messages", uniqueConstraints = @UniqueConstraint(
        name = "uk_messages_sender_client_id", columnNames = { "sender_id", "client_message_id" }))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "conversation_id", nullable = false)
    Integer conversationId;

    @Column(name = "sender_id", nullable = false)
    Integer senderId;

    @Column(name = "reply_to_message_id")
    Integer replyToMessageId;

    @Column(name = "client_message_id", columnDefinition = "CHAR(36)", nullable = false)
    String clientMessageId;

    @Column(name = "created_at", columnDefinition = "VARCHAR(19)", nullable = false)
    String createdAt;

    @Column(name = "edited_at", columnDefinition = "VARCHAR(19)")
    String editedAt;

    @Column(name = "deleted_at", columnDefinition = "VARCHAR(19)")
    String deletedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "`type`", columnDefinition = "VARCHAR(8)", nullable = false)
    MessageTypeEnum type;

    @Column(columnDefinition = "MEDIUMTEXT", nullable = false)
    String ciphertext;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(8)", nullable = false)
    MessageStatusEnum status;
}
