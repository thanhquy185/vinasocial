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
import vn.tuhoc.ContentService.domains.converters.StoryVisibilityConverter;
import vn.tuhoc.ContentService.domains.enums.StoryVisibilityEnum;

@Entity
@Table(name = "story_settings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StorySettingEntity {
    @Id
    @Column(name = "story_id", columnDefinition = "INT")
    Integer storyId;

    @Column(columnDefinition = "VARCHAR(12)", nullable = false)
    @Convert(converter = StoryVisibilityConverter.class)
    StoryVisibilityEnum visibility;

    @Column(name = "allow_like", columnDefinition = "BOOLEAN", nullable = false)
    Boolean allowLike;

    @Column(name = "allow_reply", columnDefinition = "BOOLEAN", nullable = false)
    Boolean allowReply;

    @Column(name = "allow_share", columnDefinition = "BOOLEAN", nullable = false)
    Boolean allowShare;

    @OneToOne
    @MapsId
    @JoinColumn(name = "story_id")
    StoryEntity story;
}
