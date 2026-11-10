package vn.tuhoc.RelationshipService.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationE2eeDeviceKeyRegisterRequestDTO {
    @NotBlank
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-8][0-9a-fA-F]{3}-[89aAbB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$")
    String deviceId;

    @NotBlank
    @Size(max = 120)
    String deviceName;

    @NotBlank
    @Size(max = 8192)
    @Pattern(regexp = "^\\s*\\{.*\\}\\s*$")
    String publicKey;
}
