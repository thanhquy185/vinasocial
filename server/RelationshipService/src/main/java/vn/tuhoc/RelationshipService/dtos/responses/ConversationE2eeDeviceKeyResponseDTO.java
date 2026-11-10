package vn.tuhoc.RelationshipService.dtos.responses;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationE2eeDeviceKeyResponseDTO {
    Integer userId;
    String deviceId;
    String deviceName;
    String publicKey;
    String createdAt;
    String lastSeenAt;
    String revokedAt;
    String status;
    String pairingExpiresAt;
}
