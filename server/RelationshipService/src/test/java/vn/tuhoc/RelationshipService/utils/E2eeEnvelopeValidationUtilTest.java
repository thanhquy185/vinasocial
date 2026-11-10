package vn.tuhoc.RelationshipService.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Base64;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class E2eeEnvelopeValidationUtilTest {
    private static final Base64.Encoder BASE64 = Base64.getEncoder();

    @Test
    void acceptsValidE2eeEnvelope() {
        String deviceId = UUID.randomUUID().toString();
        String envelope = """
                {"version":1,"algorithm":"AES-256-GCM+RSA-OAEP-256","iv":"%s","ciphertext":"%s","keys":{"%s":"%s"}}
                """.formatted(
                BASE64.encodeToString(new byte[12]),
                BASE64.encodeToString(new byte[16]),
                deviceId,
                BASE64.encodeToString(new byte[384]));

        assertTrue(E2eeEnvelopeValidationUtil.isValid(envelope));
    }

    @Test
    void rejectsUnencryptedPlainTextMessage() {
        assertFalse(E2eeEnvelopeValidationUtil.isValid("Xin chào"));
    }

    @Test
    void rejectsEnvelopeWithWrongRsaKeyLength() {
        String deviceId = UUID.randomUUID().toString();
        String envelope = """
                {"version":1,"algorithm":"AES-256-GCM+RSA-OAEP-256","iv":"%s","ciphertext":"%s","keys":{"%s":"%s"}}
                """.formatted(
                BASE64.encodeToString(new byte[12]),
                BASE64.encodeToString(new byte[16]),
                deviceId,
                BASE64.encodeToString(new byte[32]));

        assertFalse(E2eeEnvelopeValidationUtil.isValid(envelope));
    }
}
