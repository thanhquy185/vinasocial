package vn.tuhoc.RelationshipService.services.interfaces;

import java.util.List;

import vn.tuhoc.RelationshipService.domains.enums.ConversationParticipantRoleEnum;
import vn.tuhoc.RelationshipService.dtos.requests.GroupConversationCreateRequestDTO;
import vn.tuhoc.RelationshipService.dtos.requests.MessageCreateRequestDTO;
import vn.tuhoc.RelationshipService.dtos.requests.MessageUpdateRequestDTO;
import vn.tuhoc.RelationshipService.dtos.responses.ConversationParticipantSummaryResponseDTO;
import vn.tuhoc.RelationshipService.dtos.responses.ConversationRequestSummaryResponseDTO;
import vn.tuhoc.RelationshipService.dtos.responses.ConversationSummaryResponseDTO;
import vn.tuhoc.RelationshipService.dtos.responses.DirectMessageResponseDTO;
import vn.tuhoc.RelationshipService.dtos.responses.MessageDetailResponseDTO;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;

public interface MessagingService {
    PageResponseDTO<ConversationSummaryResponseDTO> getConversations(int page, int size);

    int markConversationAsRead(Integer conversationId);

    int markAllConversationsAsRead();

    PageResponseDTO<ConversationRequestSummaryResponseDTO> getIncomingRequests(int page, int size);

    PageResponseDTO<ConversationRequestSummaryResponseDTO> getOutgoingRequests(int page, int size);

    ConversationRequestSummaryResponseDTO acceptRequest(Integer requestId);

    ConversationRequestSummaryResponseDTO rejectRequest(Integer requestId);

    ConversationRequestSummaryResponseDTO cancelRequest(Integer requestId);

    List<ConversationParticipantSummaryResponseDTO> getParticipants(Integer conversationId);

    ConversationSummaryResponseDTO createGroup(GroupConversationCreateRequestDTO request);

    ConversationSummaryResponseDTO addParticipant(Integer conversationId, String username);

    ConversationSummaryResponseDTO updateParticipantRole(
            Integer conversationId, String username, ConversationParticipantRoleEnum role);

    ConversationSummaryResponseDTO leaveConversation(Integer conversationId);

    DirectMessageResponseDTO sendDirectMessage(String username, MessageCreateRequestDTO request);

    MessageDetailResponseDTO sendConversationMessage(Integer conversationId, MessageCreateRequestDTO request);

    PageResponseDTO<MessageDetailResponseDTO> getMessages(Integer conversationId, Integer beforeId, int size);

    MessageDetailResponseDTO editMessage(Integer conversationId, Integer messageId, MessageUpdateRequestDTO request);

    MessageDetailResponseDTO deleteMessage(Integer conversationId, Integer messageId);
}
