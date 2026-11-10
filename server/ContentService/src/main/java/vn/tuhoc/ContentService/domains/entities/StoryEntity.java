package vn.tuhoc.ContentService.domains.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.ContentService.domains.converters.StoryModerationStatusConverter;
import vn.tuhoc.ContentService.domains.converters.StoryStatusConverter;
import vn.tuhoc.ContentService.domains.enums.StoryModerationStatusEnum;
import vn.tuhoc.ContentService.domains.enums.StoryStatusEnum;

@Entity
@Table(name = "stories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@NamedEntityGraph(name = "StoryEntity.addAll", attributeNodes = {
        @NamedAttributeNode("stats"),
        @NamedAttributeNode("setting"),
})
public class StoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "public_id", columnDefinition = "VARCHAR(10)", unique = true, nullable = false)
    String publicId;

    @Column(name = "user_id", columnDefinition = "INT", nullable = false)
    Integer userId;

    @Column(name = "media_id", columnDefinition = "VARCHAR(24)", nullable = false)
    String mediaId;

    @Column(name = "created_at", columnDefinition = "VARCHAR(19)", nullable = false)
    String createdAt;

    @Column(name = "expired_at", columnDefinition = "VARCHAR(19)", nullable = false)
    String expiredAt;

    @Column(columnDefinition = "VARCHAR(8)", nullable = false)
    @Convert(converter = StoryStatusConverter.class)
    StoryStatusEnum status;

    @Column(name = "moderation_status", columnDefinition = "VARCHAR(8)", nullable = false)
    @Convert(converter = StoryModerationStatusConverter.class)
    StoryModerationStatusEnum moderationStatus;

    @OneToOne(mappedBy = "story", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    StoryStatsEntity stats;

    @OneToOne(mappedBy = "story", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    StorySettingEntity setting;
}
