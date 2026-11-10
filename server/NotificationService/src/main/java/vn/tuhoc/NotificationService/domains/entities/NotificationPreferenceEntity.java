package vn.tuhoc.NotificationService.domains.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Document(collection = "notification_preferences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationPreferenceEntity {
    @Id
    Integer userId;

    Boolean postEnabled;
    Boolean storyEnabled;
    Boolean collaboratorEnabled;
    Boolean tagEnabled;
    Boolean messageEnabled;
    Boolean followEnabled;
    Boolean replyCommentEnabled;
}
