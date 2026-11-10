package vn.tuhoc.InteractionService.services.interfaces;

import vn.tuhoc.InteractionService.domains.enums.ReportStatusEnum;
import vn.tuhoc.InteractionService.dtos.requests.ReportCreateRequestDTO;
import vn.tuhoc.InteractionService.dtos.responses.ReportDetailResponseDTO;
import vn.tuhoc.core.dtos.responses.PageResponseDTO;

public interface ReportService {
    ReportDetailResponseDTO create(ReportCreateRequestDTO request);
    PageResponseDTO<ReportDetailResponseDTO> getMyReports(int page, int size);
    PageResponseDTO<ReportDetailResponseDTO> getReports(ReportStatusEnum status, int page, int size);
    ReportDetailResponseDTO updateStatus(Integer reportId, ReportStatusEnum status);
}
