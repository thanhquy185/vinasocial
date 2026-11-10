package vn.tuhoc.RelationshipService.domains.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "message_encryption_settings")
@Getter
@Setter
public class MessageEncryptionSettingEntity {
    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;
}
