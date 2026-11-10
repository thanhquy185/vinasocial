package vn.tuhoc.InteractionService.repositories;

import vn.tuhoc.InteractionService.domains.enums.CommentStatusEnum;

public interface CommentRepositoryCustom {
    int incrementReplyCount(Integer id, CommentStatusEnum status);

    int decrementReplyCount(Integer id);

    int incrementLikeCount(Integer id, CommentStatusEnum status);

    int decrementLikeCount(Integer id);
}
