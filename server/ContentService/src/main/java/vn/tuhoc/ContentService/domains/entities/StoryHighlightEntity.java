package vn.tuhoc.ContentService.domains.entities;

import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "story_highlights")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoryHighlightEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "user_id", nullable = false)
    Integer userId;

    @Column(nullable = false, length = 30)
    String title;

    @Column(name = "created_at", nullable = false, length = 19)
    String createdAt;

    @OneToMany(mappedBy = "highlight", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    List<StoryHighlightStoryEntity> stories;
}
