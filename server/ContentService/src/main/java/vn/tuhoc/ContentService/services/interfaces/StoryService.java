package vn.tuhoc.ContentService.services.interfaces;

import java.util.List;

import vn.tuhoc.ContentService.dtos.requests.StoryUpdateModerationStatusRequestDTO;
import vn.tuhoc.ContentService.dtos.requests.StoryUpdateStatusRequestDTO;
import vn.tuhoc.ContentService.dtos.requests.StoryCreateRequestDTO;
import vn.tuhoc.ContentService.dtos.requests.StorySettingUpdateRequestDTO;
import vn.tuhoc.ContentService.dtos.requests.StoryHighlightRequestDTO;
import vn.tuhoc.ContentService.dtos.responses.StoryDetailResponseDTO;
import vn.tuhoc.ContentService.dtos.responses.StorySummaryResponseDTO;
import vn.tuhoc.ContentService.dtos.responses.StoryHighlightResponseDTO;
import vn.tuhoc.ContentService.repositories.criteria.StoryCriteria;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;

public interface StoryService {
    List<StorySummaryResponseDTO> handleGetFeed();

    List<StorySummaryResponseDTO> handleGetByUsername(String username);

    List<StoryHighlightResponseDTO> handleGetHighlights(String username);

    List<StorySummaryResponseDTO> handleGetHighlightableStories();

    StoryHighlightResponseDTO handleCreateHighlight(StoryHighlightRequestDTO request);

    StoryHighlightResponseDTO handleUpdateHighlight(Integer id, StoryHighlightRequestDTO request);

    void handleRemoveHighlight(Integer id);

    StoryDetailResponseDTO handleCreate(StoryCreateRequestDTO request);

    StoryDetailResponseDTO handleUpdateSetting(Integer id, StorySettingUpdateRequestDTO request);

    void handleRemove(Integer id);

    PageResponseDTO<StorySummaryResponseDTO> handleGetSummary(StoryCriteria storyCriteria);

    StoryDetailResponseDTO handleGetById(Integer id);

    StoryDetailResponseDTO handleUpdateStatusById(Integer id, StoryUpdateStatusRequestDTO request);

    StoryDetailResponseDTO handleUpdateModerationStatusById(
            Integer id, StoryUpdateModerationStatusRequestDTO request);
}
