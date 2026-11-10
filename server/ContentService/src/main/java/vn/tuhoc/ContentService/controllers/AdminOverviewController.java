package vn.tuhoc.ContentService.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.ContentService.dtos.responses.AdminOverviewStatsResponseDTO;
import vn.tuhoc.ContentService.services.interfaces.AdminOverviewService;
import vn.tuhoc.core.dtos.responses.RestResponseDTO;
import vn.tuhoc.core.utils.RestResponseUtil;

@RestController
@RequestMapping("/api/v1/admin/overview")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('ADMIN')")
public class AdminOverviewController {
    AdminOverviewService adminOverviewService;

    @GetMapping("/content")
    public ResponseEntity<RestResponseDTO<AdminOverviewStatsResponseDTO>> getContentStats() {
        return RestResponseUtil.ok("Truy vấn thống kê nội dung thành công.",
                this.adminOverviewService.getOverviewStats());
    }
}
