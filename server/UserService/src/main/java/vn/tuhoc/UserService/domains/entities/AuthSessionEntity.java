package vn.tuhoc.UserService.domains.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "auth_sessions", uniqueConstraints = {
        @UniqueConstraint(name = "uk_auth_sessions_session_device_id", columnNames = "session_device_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthSessionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(columnDefinition = "VARCHAR(19)", nullable = false)
    String createdAt;

    @Column(columnDefinition = "VARCHAR(19)", nullable = false)
    String expiredAt;

    @Column(columnDefinition = "VARCHAR(19)", nullable = true)
    String revokedAt;

    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    String username;

    @Column(name = "device_installation_id", columnDefinition = "VARCHAR(36)", nullable = false)
    String installationId;

    @Column(name = "session_device_id", columnDefinition = "VARCHAR(36)", nullable = false)
    String sessionDeviceId;

    @Column(name = "device_name", columnDefinition = "VARCHAR(120)", nullable = false)
    String deviceName;

    @Column(name = "last_seen_at", columnDefinition = "VARCHAR(19)", nullable = false)
    String lastSeenAt;

    @Column(name = "e2ee_device_id", columnDefinition = "VARCHAR(64)", nullable = true)
    String e2eeDeviceId;

    @Column(columnDefinition = "MEDIUMTEXT", nullable = false)
    String refreshToken;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity user;
}
