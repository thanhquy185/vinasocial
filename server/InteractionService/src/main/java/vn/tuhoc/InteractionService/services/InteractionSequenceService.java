package vn.tuhoc.InteractionService.services;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.InteractionService.domains.entities.InteractionSequenceEntity;
import vn.tuhoc.core.utils.ValidationUtil;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InteractionSequenceService {
    MongoTemplate mongoTemplate;

    private InteractionSequenceEntity increment(String sequenceName, boolean upsert) {
        return this.mongoTemplate.findAndModify(
                Query.query(Criteria.where("id").is(sequenceName)),
                new Update().inc("value", 1),
                FindAndModifyOptions.options().returnNew(true).upsert(upsert),
                InteractionSequenceEntity.class);
    }

    public Integer nextId(String sequenceName) {
        InteractionSequenceEntity sequence;
        try {
            sequence = this.increment(sequenceName, true);
        } catch (DuplicateKeyException concurrentInitialization) {
            // If several requests create a sequence at once, retry against the document created by the winner.
            sequence = this.increment(sequenceName, false);
        }
        if (ValidationUtil.isNull(sequence)) {
            throw new IllegalStateException("Could not allocate an Interaction Service id for " + sequenceName);
        }
        return Math.toIntExact(sequence.getValue());
    }
}
