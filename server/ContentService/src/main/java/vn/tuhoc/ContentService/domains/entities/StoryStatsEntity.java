package vn.tuhoc.ContentService.domains.entities;

import jakarta.persistence.Column;
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

@Entity
@Table(name = "story_stats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoryStatsEntity {
    @Id
    @Column(name = "story_id", columnDefinition = "INT")
    Integer storyId;

    @Column(name = "view_count", columnDefinition = "INT", nullable = false)
    Integer viewCount;

    @Column(name = "reply_count", columnDefinition = "INT", nullable = false)
    Integer replyCount;

    @Column(name = "share_count", columnDefinition = "INT", nullable = false)
    Integer shareCount;

    @OneToOne
    @MapsId
    @JoinColumn(name = "story_id")
    StoryEntity story;
}
