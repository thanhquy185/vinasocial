package vn.tuhoc.RelationshipService.dtos.responses;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DirectMessageResponseDTO extends MessageDetailResponseDTO {
    String conversationState;

    public DirectMessageResponseDTO(MessageDetailResponseDTO message, String conversationState) {
        super(
                message.getId(),
                message.getConversationId(),
                message.getSenderId(),
                message.getReplyToMessageId(),
                message.getClientMessageId(),
                message.getCreatedAt(),
                message.getEditedAt(),
                message.getDeletedAt(),
                message.getType(),
                message.getCiphertext(),
                message.getStatus(),
                message.getDeviceKeyWrappers());
        this.conversationState = conversationState;
    }
}
