package vn.tuhoc.InteractionService.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.InteractionService.domains.entities.SaveEntity;
import vn.tuhoc.InteractionService.dtos.responses.InteractionActionResponseDTO;
import vn.tuhoc.InteractionService.dtos.responses.PostCollectionSummaryResponseDTO;
import vn.tuhoc.InteractionService.repositories.SaveRepository;
import vn.tuhoc.InteractionService.services.interfaces.SaveService;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;
import vn.tuhoc.core.utils.PageResponseUtil;
import vn.tuhoc.core.utils.TimeUtil;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SaveServiceImplement implements SaveService {
    InteractionSequenceService sequenceService;

    SaveRepository saveRepository;

    private InteractionActionResponseDTO action(Integer postId, String action, boolean active) {
        return new InteractionActionResponseDTO(postId, "POST", action, active,
                this.saveRepository.countByPostId(postId));
    }

    @Override
    public InteractionActionResponseDTO save(Integer postId) {
        InteractionServiceSupport.requirePositiveId(postId, "postId");
        Integer userId = InteractionServiceSupport.currentUserId();
        boolean alreadyActive = this.saveRepository.existsByUserIdAndPostId(userId, postId);
        if (!alreadyActive) {
            this.saveRepository.save(SaveEntity.builder()
                .id(this.sequenceService.nextId("saves"))
                    .userId(userId)
                    .postId(postId)
                    .createdAt(TimeUtil.getCurrentDatetime())
                    .build());
        }
        return this.action(postId, alreadyActive ? "ALREADY_SAVED" : "SAVED", true);
    }

    @Override
    public InteractionActionResponseDTO unsave(Integer postId) {
        InteractionServiceSupport.requirePositiveId(postId, "postId");
        Integer userId = InteractionServiceSupport.currentUserId();
        boolean wasActive = this.saveRepository.existsByUserIdAndPostId(userId, postId);
        if (wasActive) {
            this.saveRepository.deleteByUserIdAndPostId(userId, postId);
        }
        return this.action(postId, wasActive ? "UNSAVED" : "NOT_SAVED", false);
    }

    @Override
    public InteractionActionResponseDTO getSaveStatus(Integer postId) {
        InteractionServiceSupport.requirePositiveId(postId, "postId");
        boolean active = this.saveRepository.existsByUserIdAndPostId(
                InteractionServiceSupport.currentUserId(), postId);
        return this.action(postId, active ? "SAVED" : "NOT_SAVED", active);
    }

    @Override
    public PageResponseDTO<PostCollectionSummaryResponseDTO> getMySaves(int page, int size) {
        Integer userId = InteractionServiceSupport.currentUserId();
        Pageable pageable = InteractionServiceSupport.pageable(page, size);
        Page<SaveEntity> saves = this.saveRepository.findAllByUserId(userId, pageable);
        return PageResponseUtil.convert(saves.map(item -> new PostCollectionSummaryResponseDTO(
                item.getPostId(), item.getCreatedAt())));
    }

    @Override
    public long countSaves(Integer postId) {
        InteractionServiceSupport.requirePositiveId(postId, "postId");
        return this.saveRepository.countByPostId(postId);
    }
}
