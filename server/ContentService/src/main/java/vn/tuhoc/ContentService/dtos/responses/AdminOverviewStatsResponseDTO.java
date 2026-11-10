package vn.tuhoc.ContentService.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminOverviewStatsResponseDTO {
    private long totalPosts;
    private long totalStories;
    private long flaggedPosts;
    private long rejectedPosts;
    private long flaggedStories;
    private long rejectedStories;
    private long totalViews;
    private long totalLikes;
    private long totalComments;
    private long totalReups;
    private long totalShares;
    private long totalSaves;
    private long totalStoryViews;
    private long totalStoryReplies;
    private long totalStoryShares;
}
