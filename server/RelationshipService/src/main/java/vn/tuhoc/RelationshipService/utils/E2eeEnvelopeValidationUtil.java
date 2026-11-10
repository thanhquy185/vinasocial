package vn.tuhoc.RelationshipService.utils;

import java.util.Base64;
import java.util.UUID;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class E2eeEnvelopeValidationUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Pattern DEVICE_ID_PATTERN = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    private static final String ENVELOPE_ALGORITHM = "AES-256-GCM+RSA-OAEP-256";
    private static final int AES_GCM_IV_BYTES = 12;
    private static final int AES_GCM_TAG_BYTES = 16;
    private static final int RSA_3072_CIPHERTEXT_BYTES = 384;
    private static final int MAX_DEVICE_KEYS = 256;

    private E2eeEnvelopeValidationUtil() {
    }

    public static boolean isValid(String ciphertext) {
        if (ciphertext == null || ciphertext.isBlank()) {
            return false;
        }
        try {
            JsonNode envelope = OBJECT_MAPPER.readTree(ciphertext);
            if (envelope == null
                    || !envelope.isObject()
                    || !envelope.path("version").isIntegralNumber()
                    || envelope.path("version").intValue() != 1
                    || !envelope.path("algorithm").isTextual()
                    || !ENVELOPE_ALGORITHM.equals(envelope.path("algorithm").textValue())) {
                return false;
            }

            byte[] iv = Base64.getDecoder().decode(envelope.path("iv").asText());
            byte[] messageCiphertext = Base64.getDecoder().decode(envelope.path("ciphertext").asText());
            JsonNode keys = envelope.path("keys");
            if (iv.length != AES_GCM_IV_BYTES
                    || messageCiphertext.length < AES_GCM_TAG_BYTES
                    || !keys.isObject()
                    || keys.isEmpty()
                    || keys.size() > MAX_DEVICE_KEYS) {
                return false;
            }

            var deviceKeys = keys.fields();
            while (deviceKeys.hasNext()) {
                var entry = deviceKeys.next();
                if (!DEVICE_ID_PATTERN.matcher(entry.getKey()).matches()
                        || !entry.getValue().isTextual()
                        || Base64.getDecoder().decode(entry.getValue().textValue()).length
                                != RSA_3072_CIPHERTEXT_BYTES) {
                    return false;
                }
                UUID.fromString(entry.getKey());
            }
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}
