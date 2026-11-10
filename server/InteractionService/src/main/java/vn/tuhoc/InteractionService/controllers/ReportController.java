package vn.tuhoc.InteractionService.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.InteractionService.domains.enums.ReportStatusEnum;
import vn.tuhoc.InteractionService.dtos.requests.ReportCreateRequestDTO;
import vn.tuhoc.InteractionService.dtos.requests.ReportStatusUpdateRequestDTO;
import vn.tuhoc.InteractionService.dtos.responses.ReportDetailResponseDTO;
import vn.tuhoc.InteractionService.services.interfaces.ReportService;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;
import vn.tuhoc.core.dtos.responses.RestResponseDTO;
import vn.tuhoc.core.utils.RestResponseUtil;

@RestController
@RequestMapping("/api/v1/interactions/reports")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportController {
    ReportService reportService;

    @PostMapping
    public ResponseEntity<RestResponseDTO<ReportDetailResponseDTO>> create(
            @RequestBody @Valid ReportCreateRequestDTO request) {
        return RestResponseUtil.created("Đã gửi báo cáo.", this.reportService.create(request));
    }

    @GetMapping("/me")
    public ResponseEntity<RestResponseDTO<PageResponseDTO<ReportDetailResponseDTO>>> getMyReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return RestResponseUtil.ok("Truy vấn báo cáo của bạn thành công.",
                this.reportService.getMyReports(page, size));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestResponseDTO<PageResponseDTO<ReportDetailResponseDTO>>> getReports(
            @RequestParam(required = false) ReportStatusEnum status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return RestResponseUtil.ok("Truy vấn danh sách báo cáo thành công.",
                this.reportService.getReports(status, page, size));
    }

    @PatchMapping("/{reportId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestResponseDTO<ReportDetailResponseDTO>> updateStatus(
            @PathVariable Integer reportId,
            @RequestBody @Valid ReportStatusUpdateRequestDTO request) {
        return RestResponseUtil.ok("Đã cập nhật trạng thái báo cáo.",
                this.reportService.updateStatus(reportId, request.getStatus()));
    }
}
