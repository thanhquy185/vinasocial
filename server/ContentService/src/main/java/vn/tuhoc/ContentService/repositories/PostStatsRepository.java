package vn.tuhoc.ContentService.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import vn.tuhoc.ContentService.domains.entities.PostStatsEntity;

public interface PostStatsRepository extends JpaRepository<PostStatsEntity, Integer>, JpaSpecificationExecutor<PostStatsEntity> {

    @Query("select coalesce(sum(s.viewCount), 0), coalesce(sum(s.likeCount), 0), "
            + "coalesce(sum(s.commentCount), 0), coalesce(sum(s.reupCount), 0), "
            + "coalesce(sum(s.shareCount), 0), coalesce(sum(s.saveCount), 0) from PostStatsEntity s")
    Object[] summarizeTotals();

}
