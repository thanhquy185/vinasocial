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
@Table(name = "post_stats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostStatsEntity {
    @Id
    @Column(columnDefinition = "INT")
    Integer postId;

    @Column(columnDefinition = "INT", nullable = false)
    Integer viewCount;

    @Column(columnDefinition = "INT", nullable = false)
    Integer likeCount;

    @Column(columnDefinition = "INT", nullable = false)
    Integer commentCount;

    @Column(columnDefinition = "INT", nullable = false)
    Integer reupCount;

    @Column(columnDefinition = "INT", nullable = false)
    Integer shareCount;

    @Column(columnDefinition = "INT", nullable = false)
    Integer saveCount;

    @OneToOne
    @MapsId
    @JoinColumn(name = "post_id")
    PostEntity post;
}
