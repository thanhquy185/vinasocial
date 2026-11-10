package vn.tuhoc.MediaService.services;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import vn.tuhoc.MediaService.domains.enums.MediaPurposeEnum;
import vn.tuhoc.MediaService.domains.enums.MediaTypeEnum;
import vn.tuhoc.MediaService.dtos.requests.MediaCreateRequestDTO;
import vn.tuhoc.MediaService.exceptions.MediaEncryptionPolicyViolationException;

class MediaServiceEncryptionPolicyTest {
    private final MediaServiceImplement mediaService = new MediaServiceImplement(null, null, null);

    @Test
    void rejectsContradictoryMessageMediaFlags() {
        MediaCreateRequestDTO request = new MediaCreateRequestDTO(
                null,
                MediaTypeEnum.FILE,
                MediaPurposeEnum.MESSAGE);

        assertThrows(
                MediaEncryptionPolicyViolationException.class,
                () -> mediaService.handleCreate(false, true, request));
    }

    @Test
    void rejectsE2eeFlagWhenPayloadIsNotMarkedEncrypted() {
        MediaCreateRequestDTO request = new MediaCreateRequestDTO(
                null,
                MediaTypeEnum.FILE,
                MediaPurposeEnum.POST);

        assertThrows(
                MediaEncryptionPolicyViolationException.class,
                () -> mediaService.handleCreate(false, true, request));
    }

}
