package vn.tuhoc.UserService.domains.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.UserService.domains.converters.AuthOtpPurposeConverter;
import vn.tuhoc.UserService.domains.enums.AuthOtpPurposeEnum;

@Entity
@Table(name = "auth_otps")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthOtpEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(unique = true, nullable = false)
    String publicId;

    @Column(columnDefinition = "VARCHAR(19)", nullable = false)
    String createdAt;

    @Column(columnDefinition = "VARCHAR(19)", nullable = false)
    String expiredAt;

    @Column(columnDefinition = "VARCHAR(19)", nullable = true)
    String verifiedAt;

    @Column(columnDefinition = "VARCHAR(32)", nullable = false)
    @Convert(converter = AuthOtpPurposeConverter.class)
    AuthOtpPurposeEnum purpose;

    @Column(columnDefinition = "VARCHAR(255)", nullable = true)
    String code;

    @Column(name = "failed_attempts", nullable = false)
    @Builder.Default
    Integer failedAttempts = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username_id", nullable = false)
    UsernameEntity username;
}
