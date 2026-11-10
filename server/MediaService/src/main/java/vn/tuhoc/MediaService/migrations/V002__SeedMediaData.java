package vn.tuhoc.MediaService.migrations;

// import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

// import vn.tuhoc.MediaService.domains.entities.MediaEntity;
// import vn.tuhoc.MediaService.domains.enums.MediaProviderEnum;
// import vn.tuhoc.MediaService.domains.enums.MediaPurposeEnum;
// import vn.tuhoc.MediaService.domains.enums.MediaStatusEnum;
// import vn.tuhoc.MediaService.domains.enums.MediaTypeEnum;
// import vn.tuhoc.core.utils.TimeUtil;

@ChangeUnit(id = "002-seed-media-data", order = "002", author = "quy")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class V002__SeedMediaData {
        final MongoTemplate mongoTemplate;

        @Execution
        public void execution() {
                if (!this.mongoTemplate.collectionExists("medias")) {
                        this.mongoTemplate.createCollection("medias");
                }
                // if (this.mongoTemplate.getCollection("medias").countDocuments() > 0) {
                //         return;
                // }

                // List<MediaEntity> medias = List.of(
                // // ==========================================
                // // 1. Avatar
                // // ==========================================
                // MediaEntity.builder()
                // .createdAt(TimeUtil.getCurrentDatetime())
                // .type(MediaTypeEnum.IMAGE)
                // .provider(MediaProviderEnum.MINIO)
                // .purpose(MediaPurposeEnum.AVATAR)
                // .objectKey("avatars/user-001/avatar.jpg")
                // .originalFilename("avatar.jpg")
                // .contentType("image/jpeg")
                // .width(0)
                // .height(0)

                // // Encryption - chưa tích hợp
                // .encryptionMode(null)
                // .encryptionAlgorithm(null)
                // .encryptedAesKey(null)
                // .encryptedIv(null)
                // .isEncrypted(false)

                // .status(MediaStatusEnum.SUCCEED)
                // .build(),

                // // ==========================================
                // // 2. Post Image
                // // ==========================================
                // MediaEntity.builder()
                // .createdAt(TimeUtil.getCurrentDatetime())
                // .type(MediaTypeEnum.IMAGE)
                // .provider(MediaProviderEnum.MINIO)
                // .purpose(MediaPurposeEnum.POST)
                // .objectKey("posts/2026/09/post-001.jpg")
                // .originalFilename("my-trip.jpg")
                // .contentType("image/jpeg")
                // .width(0)
                // .height(0)

                // .encryptionMode(null)
                // .encryptionAlgorithm(null)
                // .encryptedIv(null)
                // .encryptedAesKey(null)
                // .isEncrypted(false)

                // .status(MediaStatusEnum.SUCCEED)
                // .build(),

                // // ==========================================
                // // 3. Post Video / Reel
                // // ==========================================
                // MediaEntity.builder()
                // .createdAt(TimeUtil.getCurrentDatetime())
                // .type(MediaTypeEnum.VIDEO)
                // .provider(MediaProviderEnum.MINIO)
                // .purpose(MediaPurposeEnum.POST)
                // .objectKey("posts/2026/09/reel-001.mp4")
                // .originalFilename("summer-reel.mp4")
                // .contentType("video/mp4")
                // .width(0)
                // .height(0)

                // .encryptionMode(null)
                // .encryptionAlgorithm(null)
                // .encryptedIv(null)
                // .encryptedAesKey(null)
                // .isEncrypted(false)

                // .status(MediaStatusEnum.SUCCEED)
                // .build(),

                // // ==========================================
                // // 4. Story Image
                // // ==========================================
                // MediaEntity.builder()
                // .createdAt(TimeUtil.getCurrentDatetime())
                // .type(MediaTypeEnum.IMAGE)
                // .provider(MediaProviderEnum.MINIO)
                // .purpose(MediaPurposeEnum.STORY)
                // .objectKey("stories/2026/09/story-001.jpg")
                // .originalFilename("story.jpg")
                // .contentType("image/jpeg")
                // .width(0)
                // .height(0)

                // .encryptionMode(null)
                // .encryptionAlgorithm(null)
                // .encryptedIv(null)
                // .encryptedAesKey(null)
                // .isEncrypted(false)

                // .status(MediaStatusEnum.SUCCEED)
                // .build(),

                // // ==========================================
                // // 5. Story Video đang processing
                // // ==========================================
                // MediaEntity.builder()
                // .createdAt(TimeUtil.getCurrentDatetime())
                // .type(MediaTypeEnum.VIDEO)
                // .provider(MediaProviderEnum.MINIO)
                // .purpose(MediaPurposeEnum.STORY)
                // .objectKey("stories/2026/09/story-002.mp4")
                // .originalFilename("story-video.mp4")
                // .contentType("video/mp4")
                // .width(0)
                // .height(0)

                // .encryptionMode(null)
                // .encryptionAlgorithm(null)
                // .encryptedIv(null)
                // .encryptedAesKey(null)
                // .isEncrypted(false)

                // .status(MediaStatusEnum.UPLOADING)
                // .build(),

                // // ==========================================
                // // 6. Message File
                // // ==========================================
                // MediaEntity.builder()
                // .createdAt(TimeUtil.getCurrentDatetime())
                // .type(MediaTypeEnum.FILE)
                // .provider(MediaProviderEnum.MINIO)
                // .purpose(MediaPurposeEnum.MESSAGE)
                // .objectKey("messages/2026/09/document-001.pdf")
                // .originalFilename("document.pdf")
                // .contentType("application/pdf")
                // .width(0)
                // .height(0)

                // .encryptionMode(null)
                // .encryptionAlgorithm(null)
                // .encryptedIv(null)
                // .encryptedAesKey(null)
                // .isEncrypted(false)

                // .status(MediaStatusEnum.SUCCEED)
                // .build(),

                // // ==========================================
                // // 7. Message Audio
                // // ==========================================
                // MediaEntity.builder()
                // .createdAt(TimeUtil.getCurrentDatetime())
                // .type(MediaTypeEnum.AUDIO)
                // .provider(MediaProviderEnum.MINIO)
                // .purpose(MediaPurposeEnum.MESSAGE)
                // .objectKey("messages/2026/09/voice-001.mp3")
                // .originalFilename("voice-message.mp3")
                // .contentType("audio/mpeg")
                // .width(0)
                // .height(0)

                // .encryptionMode(null)
                // .encryptionAlgorithm(null)
                // .encryptedIv(null)
                // .encryptedAesKey(null)
                // .isEncrypted(false)

                // .status(MediaStatusEnum.SUCCEED)
                // .build(),

                // // ==========================================
                // // 8. Highlight Story
                // // ==========================================
                // MediaEntity.builder()
                // .createdAt(TimeUtil.getCurrentDatetime())
                // .type(MediaTypeEnum.IMAGE)
                // .provider(MediaProviderEnum.MINIO)
                // .purpose(MediaPurposeEnum.HIGHLIGHT_STORY)
                // .objectKey("highlights/user-001/travel-001.jpg")
                // .originalFilename("travel-highlight.jpg")
                // .contentType("image/jpeg")
                // .width(0)
                // .height(0)

                // .encryptionMode(null)
                // .encryptionAlgorithm(null)
                // .encryptedIv(null)
                // .encryptedAesKey(null)
                // .isEncrypted(false)

                // .status(MediaStatusEnum.SUCCEED)
                // .build());

                // this.mongoTemplate.insertAll(medias);
        }

        @RollbackExecution
        public void rollback() {
                // this.mongoTemplate.getCollection("medias").deleteMany(
                // new org.bson.Document("_id",
                // new org.bson.Document("$in", List.of(
                // "media-001",
                // "media-002",
                // "media-003",
                // "media-004",
                // "media-005",
                // "media-006",
                // "media-007",
                // "media-008"))));
        }
}