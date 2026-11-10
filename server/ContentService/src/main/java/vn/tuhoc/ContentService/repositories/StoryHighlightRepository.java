package vn.tuhoc.ContentService.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.tuhoc.ContentService.domains.entities.StoryHighlightEntity;

public interface StoryHighlightRepository extends JpaRepository<StoryHighlightEntity, Integer> {
    List<StoryHighlightEntity> findAllByUserIdOrderByIdAsc(Integer userId);
    Optional<StoryHighlightEntity> findByIdAndUserId(Integer id, Integer userId);
}
