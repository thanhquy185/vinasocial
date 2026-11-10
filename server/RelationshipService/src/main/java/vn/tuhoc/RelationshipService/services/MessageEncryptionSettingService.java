package vn.tuhoc.RelationshipService.services;

import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import vn.tuhoc.RelationshipService.domains.entities.MessageEncryptionSettingEntity;
import vn.tuhoc.RelationshipService.repositories.MessageEncryptionSettingRepository;
import vn.tuhoc.RelationshipService.exceptions.MessageEncryptionModeMismatchException;
import vn.tuhoc.RelationshipService.utils.E2eeEnvelopeValidationUtil;

@Service
@RequiredArgsConstructor
public class MessageEncryptionSettingService {
    private final MessageEncryptionSettingRepository repository;

    public boolean isEnabled(Integer userId) {
        return repository.findById(userId).map(MessageEncryptionSettingEntity::getEnabled).orElse(true);
    }

    public void validateOutgoing(Integer userId, String content) {
        validateMode(isEnabled(userId), content);
    }

    static void validateMode(boolean enabled, String content) {
        if (enabled != E2eeEnvelopeValidationUtil.isValid(content)) {
            throw new MessageEncryptionModeMismatchException();
        }
    }

    @Transactional
    public boolean setEnabled(Integer userId, boolean enabled) {
        MessageEncryptionSettingEntity setting = repository.findById(userId)
                .orElseGet(MessageEncryptionSettingEntity::new);
        setting.setUserId(userId);
        setting.setEnabled(enabled);
        repository.save(setting);
        return enabled;
    }
}
