package vn.tuhoc.InteractionService.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.InteractionService.domains.entities.ReportEntity;
import vn.tuhoc.InteractionService.domains.enums.ReportStatusEnum;
import vn.tuhoc.InteractionService.dtos.requests.ReportCreateRequestDTO;
import vn.tuhoc.InteractionService.dtos.responses.ReportDetailResponseDTO;
import vn.tuhoc.InteractionService.exceptions.InteractionException;
import vn.tuhoc.InteractionService.repositories.ReportRepository;
import vn.tuhoc.InteractionService.services.interfaces.ReportService;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;
import vn.tuhoc.core.utils.PageResponseUtil;
import vn.tuhoc.core.utils.TimeUtil;
import vn.tuhoc.core.utils.ValidationUtil;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportServiceImplement implements ReportService {
    InteractionSequenceService sequenceService;

    ReportRepository reportRepository;

    private ReportDetailResponseDTO toDetail(ReportEntity report) {
        return new ReportDetailResponseDTO(report.getId(), report.getUserId(), report.getTargetId(),
                report.getTargetType().name(), report.getCreatedAt(), report.getReason(), report.getStatus().name());
    }

    @Override
    public ReportDetailResponseDTO create(ReportCreateRequestDTO request) {
        InteractionServiceSupport.requirePositiveId(request.getTargetId(), "targetId");
        ReportEntity report = this.reportRepository.save(ReportEntity.builder()
                .id(this.sequenceService.nextId("reports"))
                .userId(InteractionServiceSupport.currentUserId())
                .targetId(request.getTargetId())
                .targetType(request.getTargetType())
                .createdAt(TimeUtil.getCurrentDatetime())
                .reason(request.getReason())
                .status(ReportStatusEnum.PENDING)
                .build());
        return this.toDetail(report);
    }

    @Override
    public PageResponseDTO<ReportDetailResponseDTO> getMyReports(int page, int size) {
        Pageable pageable = InteractionServiceSupport.pageable(page, size);
        Page<ReportEntity> reports = this.reportRepository.findAllByUserId(
                InteractionServiceSupport.currentUserId(), pageable);
        return PageResponseUtil.convert(reports.map(this::toDetail));
    }

    @Override
    public PageResponseDTO<ReportDetailResponseDTO> getReports(ReportStatusEnum status, int page, int size) {
        Pageable pageable = InteractionServiceSupport.pageable(page, size);
        Page<ReportEntity> reports = ValidationUtil.isNull(status)
                ? this.reportRepository.findAll(pageable)
                : this.reportRepository.findAllByStatus(status, pageable);
        return PageResponseUtil.convert(reports.map(this::toDetail));
    }

    @Override
    public ReportDetailResponseDTO updateStatus(Integer reportId, ReportStatusEnum status) {
        ReportEntity report = this.reportRepository.findById(reportId)
                .orElseThrow(() -> InteractionServiceSupport.notFound("REPORT_NOT_FOUND", "Không tìm thấy báo cáo."));
        ReportStatusEnum current = report.getStatus();
        if (current == status) {
            return this.toDetail(report);
        }
        boolean validTransition = (current == ReportStatusEnum.PENDING
                && (status == ReportStatusEnum.REJECTED || status == ReportStatusEnum.ACCEPTED))
                || (current == ReportStatusEnum.ACCEPTED && status == ReportStatusEnum.RESOLVED);
        if (!validTransition) {
            throw new InteractionException(HttpStatus.CONFLICT, "INVALID_REPORT_STATUS_TRANSITION",
                    "Trạng thái báo cáo không thể chuyển từ " + current + " sang " + status + ".");
        }
        report.setStatus(status);
        return this.toDetail(this.reportRepository.save(report));
    }
}
