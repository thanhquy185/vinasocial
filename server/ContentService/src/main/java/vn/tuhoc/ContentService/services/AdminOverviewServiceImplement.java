package vn.tuhoc.ContentService.services;

import org.springframework.stereotype.Service;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.ContentService.domains.enums.PostModerationStatusEnum;
import vn.tuhoc.ContentService.domains.enums.StoryModerationStatusEnum;
import vn.tuhoc.ContentService.dtos.responses.AdminOverviewStatsResponseDTO;
import vn.tuhoc.ContentService.repositories.PostRepository;
import vn.tuhoc.ContentService.repositories.PostStatsRepository;
import vn.tuhoc.ContentService.repositories.StoryRepository;
import vn.tuhoc.ContentService.repositories.StoryStatsRepository;
import vn.tuhoc.ContentService.services.interfaces.AdminOverviewService;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminOverviewServiceImplement implements AdminOverviewService {
    PostRepository postRepository;
    StoryRepository storyRepository;
    PostStatsRepository postStatsRepository;
    StoryStatsRepository storyStatsRepository;

    private long value(Object[] totals, int index) {
        if (totals == null || index >= totals.length || !(totals[index] instanceof Number number)) {
            return 0;
        }
        return number.longValue();
    }

    @Override
    public AdminOverviewStatsResponseDTO getOverviewStats() {
        Object[] postTotals = this.postStatsRepository.summarizeTotals();
        Object[] storyTotals = this.storyStatsRepository.summarizeTotals();
        return new AdminOverviewStatsResponseDTO(
                this.postRepository.count(),
                this.storyRepository.count(),
                this.postRepository.countByModerationStatus(PostModerationStatusEnum.FLAGGED),
                this.postRepository.countByModerationStatus(PostModerationStatusEnum.REJECTED),
                this.storyRepository.countByModerationStatus(StoryModerationStatusEnum.FLAGGED),
                this.storyRepository.countByModerationStatus(StoryModerationStatusEnum.REJECTED),
                this.value(postTotals, 0), this.value(postTotals, 1), this.value(postTotals, 2),
                this.value(postTotals, 3), this.value(postTotals, 4), this.value(postTotals, 5),
                this.value(storyTotals, 0), this.value(storyTotals, 1), this.value(storyTotals, 2));
    }
}
