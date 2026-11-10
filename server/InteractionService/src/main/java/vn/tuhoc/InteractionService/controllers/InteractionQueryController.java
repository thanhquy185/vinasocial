package vn.tuhoc.InteractionService.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.InteractionService.domains.enums.ShareTargetTypeEnum;
import vn.tuhoc.InteractionService.dtos.responses.TargetStatsResponseDTO;
import vn.tuhoc.InteractionService.services.interfaces.InteractionQueryService;
import vn.tuhoc.core.dtos.responses.RestResponseDTO;
import vn.tuhoc.core.utils.RestResponseUtil;

@RestController
@RequestMapping("/api/v1/interactions/targets")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InteractionQueryController {
    InteractionQueryService interactionQueryService;

    @GetMapping("/{targetType}/{targetId}/summary")
    public ResponseEntity<RestResponseDTO<TargetStatsResponseDTO>> getTargetStats(
            @PathVariable ShareTargetTypeEnum targetType,
            @PathVariable Integer targetId) {
        return RestResponseUtil.ok("Truy vấn thống kê tương tác thành công.",
                this.interactionQueryService.getTargetStats(targetType, targetId));
    }
}
