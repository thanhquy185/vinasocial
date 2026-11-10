package vn.tuhoc.InteractionService.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.InteractionService.domains.enums.ViewTargetTypeEnum;
import vn.tuhoc.InteractionService.dtos.responses.ViewSummaryResponseDTO;
import vn.tuhoc.InteractionService.services.interfaces.ViewService;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;
import vn.tuhoc.core.dtos.responses.RestResponseDTO;
import vn.tuhoc.core.utils.RestResponseUtil;

@RestController
@RequestMapping("/api/v1/interactions/views")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ViewController {
    ViewService viewService;

    @PostMapping("/{targetType}/{targetId}")
    public ResponseEntity<RestResponseDTO<Void>> recordView(
            @PathVariable ViewTargetTypeEnum targetType,
            @PathVariable Integer targetId) {
        this.viewService.recordView(targetType, targetId);
        return RestResponseUtil.ok("Đã ghi nhận lượt xem.", null);
    }

    @PostMapping("/{targetType}/me")
    public ResponseEntity<RestResponseDTO<List<Integer>>> getMyViewedTargetIds(
            @PathVariable ViewTargetTypeEnum targetType,
            @RequestBody List<Integer> targetIds) {
        return RestResponseUtil.ok("Truy vấn tin đã xem thành công.",
                this.viewService.getMyViewedTargetIds(targetType, targetIds));
    }

    @GetMapping("/{targetType}/{targetId}")
    public ResponseEntity<RestResponseDTO<PageResponseDTO<ViewSummaryResponseDTO>>> getViewers(
            @PathVariable ViewTargetTypeEnum targetType,
            @PathVariable Integer targetId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return RestResponseUtil.ok("Truy vấn lượt xem thành công.",
                this.viewService.getViewers(targetType, targetId, page, size));
    }
}
