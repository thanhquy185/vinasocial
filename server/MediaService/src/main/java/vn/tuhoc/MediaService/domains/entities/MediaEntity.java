package vn.tuhoc.MediaService.domains.entities;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import vn.tuhoc.MediaService.domains.enums.MediaEncryptionAlgorithmEnum;
import vn.tuhoc.MediaService.domains.enums.MediaEncryptionModeEnum;
import vn.tuhoc.MediaService.domains.enums.MediaProviderEnum;
import vn.tuhoc.MediaService.domains.enums.MediaPurposeEnum;
import vn.tuhoc.MediaService.domains.enums.MediaStatusEnum;
import vn.tuhoc.MediaService.domains.enums.MediaTypeEnum;

@Document(collection = "medias")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MediaEntity {
    @Id
    String id;

    @Field("user_id")
    Integer userId;

    @Field("created_at")
    String createdAt;

    @Field("type")
    MediaTypeEnum type;

    @Field("provider")
    MediaProviderEnum provider;

    @Field("purpose")
    MediaPurposeEnum purpose;

    /**
     * Object path/name trong Cloudinary / MinIO
     */
    @Field("object_key")
    String objectKey;

    /**
     * Public id trong Cloudinary / MinIO
     */
    @Field("public_id")
    String publicId;

    /**
     * Tên file gốc
     */
    @Field("original_filename")
    String originalFilename;

    /**
     * MIME type
     * image/jpeg
     * video/mp4
     * application/pdf
     */
    @Field("content_type")
    String contentType;

    /**
     * Kích thước file sau khi upload/encrypt
     */
    @Field("width")
    Integer width;

    @Field("height")
    Integer height;

    /**
     * SERVER_SIDE hoặc E2EE
     */
    @Field("encryption_mode")
    MediaEncryptionModeEnum encryptionMode;

    /**
     * AES-256-GCM
     */
    @Field("encryption_algorithm")
    MediaEncryptionAlgorithmEnum encryptionAlgorithm;

    /**
     * IV của AES-GCM
     *
     * Base64
     */
    @Field("encrypted_iv")
    String encryptedIv;

    @Field("encryption_aes_key")
    String encryptedAesKey;

    @Field("is_encrypted")
    Boolean isEncrypted;

    @Field("status")
    MediaStatusEnum status;
}