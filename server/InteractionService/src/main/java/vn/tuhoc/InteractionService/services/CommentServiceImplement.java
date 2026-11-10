package vn.tuhoc.InteractionService.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.InteractionService.domains.entities.CommentEntity;
import vn.tuhoc.InteractionService.domains.entities.CommentMention;
import vn.tuhoc.InteractionService.domains.enums.CommentStatusEnum;
import vn.tuhoc.InteractionService.domains.enums.CommentTargetTypeEnum;
import vn.tuhoc.InteractionService.dtos.requests.CommentCreateRequestDTO;
import vn.tuhoc.InteractionService.dtos.requests.CommentMentionRequestDTO;
import vn.tuhoc.InteractionService.dtos.requests.CommentUpdateRequestDTO;
import vn.tuhoc.InteractionService.dtos.responses.CommentDetailResponseDTO;
import vn.tuhoc.InteractionService.dtos.responses.CommentMentionSummaryResponseDTO;
import vn.tuhoc.InteractionService.exceptions.InteractionException;
import vn.tuhoc.InteractionService.repositories.CommentRepository;
import vn.tuhoc.InteractionService.services.interfaces.CommentService;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;
import vn.tuhoc.core.utils.PageResponseUtil;
import vn.tuhoc.core.utils.TimeUtil;
import vn.tuhoc.core.utils.ValidationUtil;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentServiceImplement implements CommentService {
    CommentRepository commentRepository;
    InteractionSequenceService sequenceService;

    private void setStatus(CommentEntity comment, CommentStatusEnum status) {
        CommentStatusEnum previousStatus = comment.getStatus();
        if (comment.getTargetType() == CommentTargetTypeEnum.COMMENT) {
            if (previousStatus == CommentStatusEnum.ACTIVE && status != CommentStatusEnum.ACTIVE) {
                this.commentRepository.decrementReplyCount(comment.getTargetId());
            } else if (previousStatus != CommentStatusEnum.ACTIVE && status == CommentStatusEnum.ACTIVE) {
                if (this.commentRepository.incrementReplyCount(comment.getTargetId(), CommentStatusEnum.ACTIVE) == 0) {
                    throw InteractionServiceSupport.notFound("PARENT_COMMENT_NOT_FOUND",
                            "Không thể khôi phục bình luận khi bình luận cha không hoạt động.");
                }
            }
        }
        comment.setStatus(status);
        comment.setUpdatedAt(TimeUtil.getCurrentDatetime());
        this.commentRepository.save(comment);
    }

    private CommentEntity getActiveComment(Integer commentId) {
        InteractionServiceSupport.requirePositiveId(commentId, "commentId");
        return this.commentRepository.findByIdAndStatus(commentId, CommentStatusEnum.ACTIVE)
                .orElseThrow(() -> InteractionServiceSupport.notFound("COMMENT_NOT_FOUND",
                        "Không tìm thấy bình luận đang hoạt động."));
    }

    private void validateAssociations(String content, List<Integer> hashtagIds,
            List<CommentMentionRequestDTO> mentions) {
        if (ValidationUtil.nonNull(hashtagIds)
                && hashtagIds.stream().anyMatch(id -> ValidationUtil.isNull(id) || id < 1)) {
            throw InteractionServiceSupport.badRequest("INVALID_HASHTAG_ID", "hashtagId phải là số nguyên dương.");
        }
        if (ValidationUtil.isNull(mentions)) {
            return;
        }
        java.util.Set<Integer> userIds = new java.util.HashSet<>();
        for (CommentMentionRequestDTO mention : mentions) {
            if (ValidationUtil.isNull(mention)
                    || ValidationUtil.isNull(mention.getUserId()) || mention.getUserId() < 1
                    || ValidationUtil.isNull(mention.getStartIndex())
                    || ValidationUtil.isNull(mention.getEndIndex())
                    || mention.getStartIndex() < 0 || mention.getEndIndex() <= mention.getStartIndex()
                    || mention.getEndIndex() > content.length()) {
                throw InteractionServiceSupport.badRequest("INVALID_MENTION_RANGE",
                        "Mention phải có userId hợp lệ và vị trí nằm trong nội dung bình luận.");
            }
            if (!userIds.add(mention.getUserId())) {
                throw InteractionServiceSupport.badRequest("DUPLICATE_MENTION",
                        "Mỗi người dùng chỉ được xuất hiện một lần trong danh sách mention.");
            }
        }
    }

    private void replaceHashtags(CommentEntity comment, List<Integer> hashtagIds) {
        comment.setHashtagIds(ValidationUtil.isNull(hashtagIds) ? List.of() : hashtagIds.stream().distinct().toList());
    }

    private void replaceMentions(CommentEntity comment, List<CommentMentionRequestDTO> mentions) {
        comment.setMentions(ValidationUtil.isNull(mentions) ? List.of() : mentions.stream()
                .map(mention -> new CommentMention(
                        mention.getUserId(), mention.getStartIndex(), mention.getEndIndex()))
                .toList());
    }

    private CommentDetailResponseDTO toDetail(CommentEntity comment) {
        List<Integer> hashtags = ValidationUtil.isNull(comment.getHashtagIds()) ? List.of() : comment.getHashtagIds();
        List<CommentMentionSummaryResponseDTO> mentions = ValidationUtil.isNull(comment.getMentions())
                ? List.of()
                : comment.getMentions().stream()
                        .map(item -> new CommentMentionSummaryResponseDTO(
                                item.getUserId(), item.getStartIndex(), item.getEndIndex()))
                        .toList();
        return new CommentDetailResponseDTO(
                comment.getId(), comment.getUserId(), comment.getTargetId(), comment.getTargetType().name(),
                comment.getCreatedAt(), comment.getUpdatedAt(), comment.getContent(), comment.getLikeCount(),
                comment.getReplyCount(), comment.getStatus().name(), hashtags, mentions);
    }

    @Override
    public CommentDetailResponseDTO create(CommentCreateRequestDTO request) {
        Integer userId = InteractionServiceSupport.currentUserId();
        InteractionServiceSupport.requirePositiveId(request.getTargetId(), "targetId");
        this.validateAssociations(request.getContent(), request.getHashtagIds(), request.getMentions());
        if (request.getTargetType() == CommentTargetTypeEnum.COMMENT) {
            this.getActiveComment(request.getTargetId());
        }

        String now = TimeUtil.getCurrentDatetime();
        CommentEntity comment = CommentEntity.builder()
                .id(this.sequenceService.nextId("comments"))
                .userId(userId)
                .targetId(request.getTargetId())
                .targetType(request.getTargetType())
                .createdAt(now)
                .updatedAt(now)
                .content(request.getContent())
                .likeCount(0)
                .replyCount(0)
                .status(CommentStatusEnum.ACTIVE)
                .build();
        this.replaceHashtags(comment, request.getHashtagIds());
        this.replaceMentions(comment, request.getMentions());
        comment = this.commentRepository.save(comment);

        if (request.getTargetType() == CommentTargetTypeEnum.COMMENT
                && this.commentRepository.incrementReplyCount(request.getTargetId(), CommentStatusEnum.ACTIVE) == 0) {
            throw InteractionServiceSupport.notFound("COMMENT_NOT_FOUND", "Không tìm thấy bình luận đang hoạt động.");
        }
        return this.toDetail(comment);
    }

    @Override
    public CommentDetailResponseDTO update(Integer commentId, CommentUpdateRequestDTO request) {
        Integer userId = InteractionServiceSupport.currentUserId();
        CommentEntity comment = this.getActiveComment(commentId);
        if (!comment.getUserId().equals(userId)) {
            throw new AccessDeniedException("Bạn chỉ có thể chỉnh sửa bình luận của mình.");
        }
        this.validateAssociations(request.getContent(), request.getHashtagIds(), request.getMentions());
        comment.setContent(request.getContent());
        comment.setUpdatedAt(TimeUtil.getCurrentDatetime());
        if (ValidationUtil.nonNull(request.getHashtagIds())) {
            this.replaceHashtags(comment, request.getHashtagIds());
        }
        if (ValidationUtil.nonNull(request.getMentions())) {
            this.replaceMentions(comment, request.getMentions());
        }
        comment = this.commentRepository.save(comment);
        return this.toDetail(comment);
    }

    @Override
    public CommentDetailResponseDTO delete(Integer commentId) {
        Integer userId = InteractionServiceSupport.currentUserId();
        CommentEntity comment = this.commentRepository.findById(commentId)
                .orElseThrow(() -> InteractionServiceSupport.notFound("COMMENT_NOT_FOUND", "Không tìm thấy bình luận."));
        if (comment.getStatus() == CommentStatusEnum.REMOVED) {
            throw InteractionServiceSupport.notFound("COMMENT_NOT_FOUND", "Không tìm thấy bình luận.");
        }
        if (!comment.getUserId().equals(userId)) {
            throw new AccessDeniedException("Bạn chỉ có thể xóa bình luận của mình.");
        }
        this.setStatus(comment, CommentStatusEnum.REMOVED);
        return this.toDetail(comment);
    }

    @Override
    public CommentDetailResponseDTO updateStatus(Integer commentId, CommentStatusEnum status) {
        CommentEntity comment = this.commentRepository.findById(commentId)
                .orElseThrow(() -> InteractionServiceSupport.notFound("COMMENT_NOT_FOUND", "Không tìm thấy bình luận."));
        if (comment.getStatus() == CommentStatusEnum.REMOVED && status != CommentStatusEnum.REMOVED) {
            throw new InteractionException(HttpStatus.CONFLICT, "COMMENT_STATUS_FINAL",
                    "Bình luận đã bị gỡ và không thể khôi phục.");
        }
        if (comment.getStatus() != status) {
            this.setStatus(comment, status);
        }
        return this.toDetail(comment);
    }

    @Override
    public CommentDetailResponseDTO getById(Integer commentId) {
        return this.toDetail(this.getActiveComment(commentId));
    }

    @Override
    public PageResponseDTO<CommentDetailResponseDTO> getComments(
            CommentTargetTypeEnum targetType, Integer targetId, int page, int size) {
        InteractionServiceSupport.requirePositiveId(targetId, "targetId");
        Pageable pageable = InteractionServiceSupport.pageable(page, size);
        Page<CommentEntity> comments = this.commentRepository.findAllByTargetTypeAndTargetIdAndStatus(
                targetType, targetId, CommentStatusEnum.ACTIVE, pageable);
        return PageResponseUtil.convert(comments.map(this::toDetail));
    }

    @Override
    public PageResponseDTO<CommentDetailResponseDTO> getReplies(Integer commentId, int page, int size) {
        this.getActiveComment(commentId);
        Pageable pageable = InteractionServiceSupport.pageable(page, size);
        Page<CommentEntity> replies = this.commentRepository.findAllByTargetTypeAndTargetIdAndStatus(
                CommentTargetTypeEnum.COMMENT, commentId, CommentStatusEnum.ACTIVE, pageable);
        return PageResponseUtil.convert(replies.map(this::toDetail));
    }

    @Override
    public long countComments(CommentTargetTypeEnum targetType, Integer targetId) {
        InteractionServiceSupport.requirePositiveId(targetId, "targetId");
        return this.commentRepository.countByTargetTypeAndTargetIdAndStatus(
                targetType, targetId, CommentStatusEnum.ACTIVE);
    }
}
