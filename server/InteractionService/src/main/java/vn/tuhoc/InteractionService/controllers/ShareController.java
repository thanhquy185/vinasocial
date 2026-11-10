package vn.tuhoc.InteractionService.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.InteractionService.domains.enums.ShareTargetTypeEnum;
import vn.tuhoc.InteractionService.dtos.requests.ShareCreateRequestDTO;
import vn.tuhoc.InteractionService.dtos.responses.ShareSummaryResponseDTO;
import vn.tuhoc.InteractionService.services.interfaces.ShareService;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;
import vn.tuhoc.core.dtos.responses.RestResponseDTO;
import vn.tuhoc.core.utils.RestResponseUtil;

@RestController
@RequestMapping("/api/v1/interactions/shares")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShareController {
    ShareService shareService;

    @PostMapping
    public ResponseEntity<RestResponseDTO<ShareSummaryResponseDTO>> create(
            @RequestBody @Valid ShareCreateRequestDTO request) {
        return RestResponseUtil.created("Đã ghi nhận lượt chia sẻ.", this.shareService.create(request));
    }

    @GetMapping
    public ResponseEntity<RestResponseDTO<PageResponseDTO<ShareSummaryResponseDTO>>> getShares(
            @RequestParam ShareTargetTypeEnum targetType,
            @RequestParam Integer targetId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return RestResponseUtil.ok("Truy vấn danh sách chia sẻ thành công.",
                this.shareService.getShares(targetType, targetId, page, size));
    }
}
