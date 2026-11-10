// package vn.tuhoc.MediaService.domains.entities;

// import org.springframework.data.annotation.Id;
// import org.springframework.data.mongodb.core.mapping.Document;
// import org.springframework.data.mongodb.core.mapping.Field;

// import lombok.AccessLevel;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import lombok.experimental.FieldDefaults;
// import vn.tuhoc.MediaService.domains.enums.MediaEncryptionAlgorithmEnum;
// import vn.tuhoc.MediaService.domains.enums.MediaEncryptionKeyTypeEnum;

// @Document(collection = "media_encryption_keys")
// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
// @FieldDefaults(level = AccessLevel.PRIVATE)
// public class MediaEncryptionKeyEntity {
// @Id
// String id;

// @Field("created_at")
// String createdAt;

// @Field("media_id")
// Long mediaId;

// @Field("recipient_user_id")
// Long recipientUserId;

// @Field("encryption_key_type")
// MediaEncryptionKeyTypeEnum encryptionKeyType;

// @Field("encryption_algorithm")
// MediaEncryptionAlgorithmEnum encryptionAlgorithm;

// @Field("encrypted_key")
// String encryptedKey;

// @Field("wrapping_key_version")
// String wrappingKeyVersion;
// }
