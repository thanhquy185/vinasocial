package vn.tuhoc.NotificationService.migrations;

import org.springframework.data.mongodb.core.MongoTemplate;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@ChangeUnit(id = "001-create-notification-collections", order = "001", author = "quy")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class V001__CreateNotificationCollections {
    final MongoTemplate mongoTemplate;

    @Execution
    public void execution() {
        if (!this.mongoTemplate.collectionExists("notifications")) {
            this.mongoTemplate.createCollection("notifications");
        }
        if (!this.mongoTemplate.collectionExists("notification_preferences")) {
            this.mongoTemplate.createCollection("notification_preferences");
        }
    }

    @RollbackExecution
    public void rollback() {
        // Keep existing notification data when rolling back collection setup.
    }
}
