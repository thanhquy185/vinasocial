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
public class ConversationSummaryResponseDTO {
    Integer id;
    String type;
    String name;
    Integer lastMessageId;
    String lastActivityAt;
    Integer lastReadMessageId;
}
