package vn.tuhoc.ContentService.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.tuhoc.ContentService.domains.entities.StoryStatsEntity;

public interface StoryStatsRepository extends JpaRepository<StoryStatsEntity, Integer> {
    @Query("select coalesce(sum(s.viewCount), 0), coalesce(sum(s.replyCount), 0), "
            + "coalesce(sum(s.shareCount), 0) from StoryStatsEntity s")
    Object[] summarizeTotals();
}
