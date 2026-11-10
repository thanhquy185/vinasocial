package vn.tuhoc.RelationshipService.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import vn.tuhoc.RelationshipService.domains.entities.ConversationEntity;
import vn.tuhoc.RelationshipService.domains.entities.ConversationRequestEntity;
import vn.tuhoc.RelationshipService.domains.enums.ConversationRequestStatusEnum;
import vn.tuhoc.RelationshipService.domains.enums.ConversationTypeEnum;
import vn.tuhoc.RelationshipService.repositories.BlockRepository;
import vn.tuhoc.RelationshipService.repositories.ConversationE2eeDeviceKeyRepository;
import vn.tuhoc.RelationshipService.repositories.ConversationMessageDeviceKeyWrapperRepository;
import vn.tuhoc.RelationshipService.repositories.ConversationParticipantRepository;
import vn.tuhoc.RelationshipService.repositories.ConversationRepository;
import vn.tuhoc.RelationshipService.repositories.ConversationRequestRepository;
import vn.tuhoc.RelationshipService.repositories.MessageRepository;

class E2eeMessageKeyWrapperServiceTest {
    @Test
    void pendingRecipientCanTransferOwnHistoryButUnrelatedUserCannot() {
        ConversationRepository conversations = mock(ConversationRepository.class);
        ConversationRequestRepository requests = mock(ConversationRequestRepository.class);
        ConversationParticipantRepository participants = mock(ConversationParticipantRepository.class);
        ConversationEntity conversation = ConversationEntity.builder().id(11)
                .type(ConversationTypeEnum.DIRECT).directUserLowId(3).directUserHighId(7).build();
        ConversationRequestEntity request = ConversationRequestEntity.builder().conversationId(11)
                .senderId(3).receiverId(7).status(ConversationRequestStatusEnum.PENDING).build();
        when(conversations.findById(11)).thenReturn(Optional.of(conversation));
        when(requests.findByConversationId(11)).thenReturn(Optional.of(request));
        E2eeMessageKeyWrapperService service = new E2eeMessageKeyWrapperService(
                mock(ConversationE2eeDeviceKeyRepository.class),
                mock(ConversationMessageDeviceKeyWrapperRepository.class),
                mock(MessageRepository.class), conversations, requests, participants,
                mock(BlockRepository.class));

        assertTrue(Boolean.TRUE.equals(ReflectionTestUtils.invokeMethod(service, "canReadConversation", 7, 11)));
        assertFalse(Boolean.TRUE.equals(ReflectionTestUtils.invokeMethod(service, "canReadConversation", 9, 11)));
    }
}
