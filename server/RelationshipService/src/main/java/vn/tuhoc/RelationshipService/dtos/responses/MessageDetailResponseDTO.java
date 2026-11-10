package vn.tuhoc.RelationshipService.dtos.responses;

import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageDetailResponseDTO {
    Integer id;
    Integer conversationId;
    Integer senderId;
    Integer replyToMessageId;
    String clientMessageId;
    String createdAt;
    String editedAt;
    String deletedAt;
    String type;
    String ciphertext;
    String status;
    Map<String, String> deviceKeyWrappers;
}
