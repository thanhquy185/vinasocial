package vn.tuhoc.InteractionService.repositories;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.client.result.UpdateResult;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.InteractionService.domains.entities.CommentEntity;
import vn.tuhoc.InteractionService.domains.enums.CommentStatusEnum;
import vn.tuhoc.core.utils.ValidationUtil;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {
    MongoTemplate mongoTemplate;

    @Override
    public int incrementReplyCount(Integer id, CommentStatusEnum status) {
        return this.updateCount(id, status, "replyCount", 1, false);
    }

    @Override
    public int decrementReplyCount(Integer id) {
        return this.updateCount(id, null, "replyCount", -1, true);
    }

    @Override
    public int incrementLikeCount(Integer id, CommentStatusEnum status) {
        return this.updateCount(id, status, "likeCount", 1, false);
    }

    @Override
    public int decrementLikeCount(Integer id) {
        return this.updateCount(id, null, "likeCount", -1, true);
    }

    private int updateCount(Integer id, CommentStatusEnum status, String field, int change, boolean requirePositiveCount) {
        Criteria criteria = Criteria.where("id").is(id);
        if (ValidationUtil.nonNull(status)) {
            criteria = criteria.and("status").is(status);
        }
        if (requirePositiveCount) {
            criteria = criteria.and(field).gt(0);
        }
        UpdateResult result = this.mongoTemplate.updateFirst(
                Query.query(criteria),
                new Update().inc(field, change),
                CommentEntity.class);
        return Math.toIntExact(result.getMatchedCount());
    }
}
