package vn.tuhoc.MediaService.dtos.responses;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.MediaService.domains.enums.MediaEncryptionAlgorithmEnum;
import vn.tuhoc.MediaService.domains.enums.MediaEncryptionModeEnum;
import vn.tuhoc.MediaService.domains.enums.MediaProviderEnum;
import vn.tuhoc.MediaService.domains.enums.MediaPurposeEnum;
import vn.tuhoc.MediaService.domains.enums.MediaStatusEnum;
import vn.tuhoc.MediaService.domains.enums.MediaTypeEnum;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MediaDetailResponseDTO {
    String id;

    String createdAt;

    MediaTypeEnum type;

    MediaProviderEnum provider;

    MediaPurposeEnum purpose;

    String objectKey;

    String originalFilename;

    String contentType;

    Integer width;

    Integer height;

    MediaEncryptionModeEnum encryptionMode;

    MediaEncryptionAlgorithmEnum encryptionAlgorithm;

    String encryptedIv;

    String encryptedAesKey;

    Boolean isEncrypted;

    MediaStatusEnum status;
}
