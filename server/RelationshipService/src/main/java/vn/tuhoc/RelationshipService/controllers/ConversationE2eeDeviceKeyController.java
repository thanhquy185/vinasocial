package vn.tuhoc.RelationshipService.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.RelationshipService.dtos.requests.ConversationE2eeDeviceKeyRegisterRequestDTO;
import vn.tuhoc.RelationshipService.dtos.requests.ConversationE2eeHistoryKeyWrapperBatchRequestDTO;
import vn.tuhoc.RelationshipService.dtos.requests.ConversationE2eePairingCompleteRequestDTO;
import vn.tuhoc.RelationshipService.dtos.responses.ConversationE2eeDevicePublicKeyResponseDTO;
import vn.tuhoc.RelationshipService.dtos.responses.ConversationE2eeDeviceKeyResponseDTO;
import vn.tuhoc.RelationshipService.dtos.responses.ConversationE2eePairingCodeResponseDTO;
import vn.tuhoc.RelationshipService.services.ConversationE2eeDeviceKeyService;
import vn.tuhoc.RelationshipService.services.E2eeMessageKeyWrapperService;
import vn.tuhoc.core.dtos.responses.RestResponseDTO;
import vn.tuhoc.core.utils.RestResponseUtil;

@RestController
@RequestMapping("/api/v1/conversations/e2ee/keys")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConversationE2eeDeviceKeyController {
    ConversationE2eeDeviceKeyService deviceKeyService;
    E2eeMessageKeyWrapperService messageKeyWrapperService;

    public record RebindCompleteRequest(@NotBlank String answer) { }

    @PutMapping("/me")
    public ResponseEntity<RestResponseDTO<ConversationE2eeDeviceKeyResponseDTO>> register(
            @RequestBody @Valid ConversationE2eeDeviceKeyRegisterRequestDTO request) {
        return RestResponseUtil.ok("Đã đăng ký yêu cầu thiết bị mã hóa.",
                this.deviceKeyService.register(request));
    }

    @PostMapping("/me/{deviceId}/rebind/challenge")
    public ResponseEntity<RestResponseDTO<String>> startRebind(@PathVariable String deviceId) {
        return RestResponseUtil.ok("Đã tạo thử thách khôi phục thiết bị.",
                this.deviceKeyService.startRebind(deviceId));
    }

    @PostMapping("/me/{deviceId}/rebind/complete")
    public ResponseEntity<RestResponseDTO<ConversationE2eeDeviceKeyResponseDTO>> completeRebind(
            @PathVariable String deviceId, @RequestBody @Valid RebindCompleteRequest request) {
        return RestResponseUtil.ok("Đã gắn khóa E2EE với phiên đăng nhập hiện tại.",
                this.deviceKeyService.completeRebind(deviceId, request.answer()));
    }

    @PostMapping("/me/{deviceId}/pairing/approve")
    public ResponseEntity<RestResponseDTO<ConversationE2eePairingCodeResponseDTO>> approvePairing(
            @PathVariable String deviceId) {
        return RestResponseUtil.ok("Đã duyệt thiết bị. Hãy nhập mã chỉ hiển thị một lần này trên thiết bị mới.",
                this.deviceKeyService.approvePairing(deviceId));
    }

    @PostMapping("/me/{deviceId}/pairing/complete")
    public ResponseEntity<RestResponseDTO<ConversationE2eeDeviceKeyResponseDTO>> completePairing(
            @PathVariable String deviceId,
            @RequestBody @Valid ConversationE2eePairingCompleteRequestDTO request) {
        return RestResponseUtil.ok("Đã xác nhận và kích hoạt thiết bị mã hóa mới.",
                this.deviceKeyService.completePairing(deviceId, request.getPairingCode()));
    }

    @PostMapping("/me/{deviceId}/history-wrappers")
    public ResponseEntity<RestResponseDTO<Integer>> addHistoryKeyWrappers(
            @PathVariable String deviceId,
            @RequestBody @Valid ConversationE2eeHistoryKeyWrapperBatchRequestDTO request) {
        return RestResponseUtil.ok("Đã chuyển các khóa giải mã lịch sử cho thiết bị.",
                this.messageKeyWrapperService.addHistoryKeyWrappers(deviceId, request.getWrappers()));
    }

    @GetMapping("/me")
    public ResponseEntity<RestResponseDTO<List<ConversationE2eeDeviceKeyResponseDTO>>> getMyDevices() {
        return RestResponseUtil.ok("Đã truy vấn danh sách thiết bị nhắn tin.",
                this.deviceKeyService.getMyDevices());
    }

    @GetMapping
    public ResponseEntity<RestResponseDTO<List<ConversationE2eeDevicePublicKeyResponseDTO>>> getPublicKeys(
            @RequestParam List<Integer> userIds) {
        if (userIds.isEmpty() || userIds.size() > 50 || userIds.stream().anyMatch(userId -> userId == null || userId < 1)) {
            return RestResponseUtil.badRequest(
                    "INVALID_E2EE_KEY_QUERY",
                    "Danh sách tài khoản cần truy vấn khóa không hợp lệ.");
        }
        return RestResponseUtil.ok("Đã truy vấn khóa công khai.",
                this.deviceKeyService.getPublicKeys(userIds));
    }

    @DeleteMapping("/me/{deviceId}")
    public ResponseEntity<RestResponseDTO<Integer>> revoke(@PathVariable String deviceId) {
        return RestResponseUtil.ok("Đã thu hồi khóa của thiết bị.",
                this.deviceKeyService.revoke(deviceId));
    }
}
