package vn.tuhoc.RelationshipService.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Base64;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import vn.tuhoc.RelationshipService.exceptions.MessageEncryptionModeMismatchException;

class MessageEncryptionSettingServiceTest {
    private static String envelope() {
        return """
                {"version":1,"algorithm":"AES-256-GCM+RSA-OAEP-256","iv":"%s","ciphertext":"%s","keys":{"%s":"%s"}}
                """.formatted(
                Base64.getEncoder().encodeToString(new byte[12]),
                Base64.getEncoder().encodeToString(new byte[16]),
                UUID.randomUUID(),
                Base64.getEncoder().encodeToString(new byte[384]));
    }

    @Test
    void enabledModeAcceptsOnlyEnvelope() {
        assertDoesNotThrow(() -> MessageEncryptionSettingService.validateMode(true, envelope()));
        assertThrows(MessageEncryptionModeMismatchException.class,
                () -> MessageEncryptionSettingService.validateMode(true, "Xin chào"));
    }

    @Test
    void disabledModeAcceptsOnlyPlainContent() {
        assertDoesNotThrow(() -> MessageEncryptionSettingService.validateMode(false, "Xin chào"));
        assertThrows(MessageEncryptionModeMismatchException.class,
                () -> MessageEncryptionSettingService.validateMode(false, envelope()));
    }
}
