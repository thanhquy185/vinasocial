package vn.tuhoc.RelationshipService.dtos.responses;

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
public class ConversationRequestSummaryResponseDTO {
    Integer id;
    Integer conversationId;
    Integer senderId;
    Integer receiverId;
    String createdAt;
    String status;
}
