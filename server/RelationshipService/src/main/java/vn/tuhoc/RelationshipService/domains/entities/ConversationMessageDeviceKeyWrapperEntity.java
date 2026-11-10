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
@Table(name = "conversation_message_device_key_wrappers", uniqueConstraints = @UniqueConstraint(
        name = "uk_message_device_key_wrapper", columnNames = { "message_id", "device_id" }))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationMessageDeviceKeyWrapperEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "message_id", nullable = false)
    Integer messageId;

    @Column(name = "device_id", columnDefinition = "CHAR(36)", nullable = false)
    String deviceId;

    @Column(name = "wrapped_key", length = 512, nullable = false)
    String wrappedKey;

    @Column(name = "ciphertext_hash", columnDefinition = "CHAR(64)", nullable = false)
    String ciphertextHash;

    @Column(name = "created_at", columnDefinition = "VARCHAR(19)", nullable = false)
    String createdAt;
}
