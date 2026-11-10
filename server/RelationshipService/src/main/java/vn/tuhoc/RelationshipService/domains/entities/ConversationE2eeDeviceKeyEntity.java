package vn.tuhoc.RelationshipService.domains.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "conversation_e2ee_device_keys")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationE2eeDeviceKeyEntity {
    @Id
    @Column(name = "device_id", columnDefinition = "CHAR(36)")
    String deviceId;

    @Column(name = "device_name", length = 120, nullable = false)
    String deviceName;

    @Column(name = "user_id", nullable = false)
    Integer userId;

    @Column(name = "public_key", columnDefinition = "TEXT", nullable = false)
    String publicKey;

    @Column(name = "created_at", columnDefinition = "VARCHAR(19)", nullable = false)
    String createdAt;

    @Column(name = "last_seen_at", columnDefinition = "VARCHAR(19)", nullable = false)
    String lastSeenAt;

    @Column(name = "revoked_at", columnDefinition = "VARCHAR(19)")
    String revokedAt;

    @Column(name = "status", length = 32, nullable = false)
    String status;

    @Column(name = "auth_session_device_id", length = 36)
    String authSessionDeviceId;

    @Column(name = "pairing_code_mac", length = 64)
    String pairingCodeMac;

    @Column(name = "pairing_expires_at", columnDefinition = "VARCHAR(19)")
    String pairingExpiresAt;

    @Column(name = "pairing_failed_attempts", nullable = false)
    Integer pairingFailedAttempts;

    @Column(name = "rebind_challenge_mac", length = 64)
    String rebindChallengeMac;

    @Column(name = "rebind_expires_at", columnDefinition = "VARCHAR(19)")
    String rebindExpiresAt;

    @Column(name = "rebind_session_device_id", length = 36)
    String rebindSessionDeviceId;
}
