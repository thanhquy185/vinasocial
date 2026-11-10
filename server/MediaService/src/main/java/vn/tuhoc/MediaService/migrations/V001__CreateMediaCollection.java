package vn.tuhoc.MediaService.migrations;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;

import org.springframework.data.mongodb.core.MongoTemplate;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@ChangeUnit(id = "001-create-media-collection", order = "001", author = "quy")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class V001__CreateMediaCollection {
    final MongoTemplate mongoTemplate;

    @Execution
    public void execution() {
        if (!this.mongoTemplate.collectionExists("medias")) {
            this.mongoTemplate.createCollection("medias");
        }
    }

    @RollbackExecution
    public void rollback() {
        // if (this.mongoTemplate.collectionExists("medias")) {
        // this.mongoTemplate.dropCollection("medias");
        // }
    }
}
