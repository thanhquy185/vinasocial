package vn.tuhoc.RelationshipService.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;

import vn.tuhoc.RelationshipService.domains.entities.ConversationE2eeDeviceKeyEntity;
import vn.tuhoc.RelationshipService.exceptions.E2eeDeviceKeyConflictException;
import vn.tuhoc.RelationshipService.repositories.ConversationE2eeDeviceKeyRepository;

class ConversationE2eeDeviceKeyServiceTest {
    private static final String DEVICE_ID = "123e4567-e89b-42d3-a456-426614174000";

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void rebindRequiresPrivateKeyAndMovesDeviceToNewSession() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(3072);
        KeyPair pair = generator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) pair.getPublic();
        String jwk = "{\"kty\":\"RSA\",\"n\":\"%s\",\"e\":\"%s\"}".formatted(
                base64Url(publicKey.getModulus()), base64Url(publicKey.getPublicExponent()));
        ConversationE2eeDeviceKeyEntity device = ConversationE2eeDeviceKeyEntity.builder()
                .deviceId(DEVICE_ID).userId(7).deviceName("Test device")
                .publicKey(jwk).status("ACTIVE").authSessionDeviceId("session-old")
                .pairingFailedAttempts(0).build();
        ConversationE2eeDeviceKeyRepository repository = mock(ConversationE2eeDeviceKeyRepository.class);
        when(repository.findById(DEVICE_ID)).thenReturn(Optional.of(device));
        when(repository.save(any(ConversationE2eeDeviceKeyEntity.class))).thenAnswer(call -> call.getArgument(0));
        ConversationE2eeDeviceKeyService service = new ConversationE2eeDeviceKeyService(repository);
        ReflectionTestUtils.setField(service, "jwtBase64Secret", Base64.getEncoder()
                .encodeToString("test-secret-with-at-least-32-bytes".getBytes(StandardCharsets.UTF_8)));
        setSession("session-new");

        String encrypted = service.startRebind(DEVICE_ID);
        assertThrows(E2eeDeviceKeyConflictException.class,
                () -> service.completeRebind(DEVICE_ID, "wrong-answer"));
        assertEquals("session-old", device.getAuthSessionDeviceId());

        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
        cipher.init(Cipher.DECRYPT_MODE, pair.getPrivate(), new OAEPParameterSpec(
                "SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT));
        String answer = new String(cipher.doFinal(Base64.getDecoder().decode(encrypted)), StandardCharsets.US_ASCII);
        service.completeRebind(DEVICE_ID, answer);
        assertEquals("session-new", device.getAuthSessionDeviceId());
        assertEquals(null, device.getRebindChallengeMac());
        assertThrows(E2eeDeviceKeyConflictException.class,
                () -> service.completeRebind(DEVICE_ID, answer));
    }

    private static String base64Url(BigInteger value) {
        byte[] bytes = value.toByteArray();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(
                bytes[0] == 0 ? java.util.Arrays.copyOfRange(bytes, 1, bytes.length) : bytes);
    }

    private static void setSession(String sessionDeviceId) {
        Jwt jwt = Jwt.withTokenValue("test-token").header("alg", "none")
                .claim("user", Map.of("id", 7, "sessionDeviceId", sessionDeviceId)).build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
    }
}
