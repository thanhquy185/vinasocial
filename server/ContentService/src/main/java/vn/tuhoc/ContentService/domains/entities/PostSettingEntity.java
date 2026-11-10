package vn.tuhoc.ContentService.domains.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.ContentService.domains.converters.PostSettingPrivacyConverter;
import vn.tuhoc.ContentService.domains.enums.PostSettingPrivacyEnum;

@Entity
@Table(name = "post_settings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostSettingEntity {
    @Id
    @Column(columnDefinition = "INT")
    Integer postId;

    @Column(columnDefinition = "VARCHAR(12)", nullable = false)
    @Convert(converter = PostSettingPrivacyConverter.class)
    PostSettingPrivacyEnum privacy;

    @Column(columnDefinition = "BOOLEAN", nullable = false)
    Boolean isPinned;

    @Column(columnDefinition = "BOOLEAN", nullable = false)
    Boolean allowComment;

    @Column(columnDefinition = "BOOLEAN", nullable = false)
    Boolean allowReup;

    @Column(columnDefinition = "BOOLEAN", nullable = false)
    Boolean allowShare;

    @Column(columnDefinition = "BOOLEAN", nullable = false)
    Boolean allowSave;

    @Column(columnDefinition = "BOOLEAN", nullable = false)
    Boolean showViewCount;

    @Column(columnDefinition = "BOOLEAN", nullable = false)
    Boolean showLikeCount;

    @Column(columnDefinition = "BOOLEAN", nullable = false)
    Boolean showCommentCount;

    @Column(columnDefinition = "BOOLEAN", nullable = false)
    Boolean showReupCount;

    @Column(columnDefinition = "BOOLEAN", nullable = false)
    Boolean showShareCount;

    @Column(columnDefinition = "BOOLEAN", nullable = false)
    Boolean showSaveCount;

    @OneToOne
    @MapsId
    @JoinColumn(name = "post_id")
    PostEntity post;
}
