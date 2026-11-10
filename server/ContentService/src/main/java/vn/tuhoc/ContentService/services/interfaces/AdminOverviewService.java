package vn.tuhoc.ContentService.services.interfaces;

import vn.tuhoc.ContentService.dtos.responses.AdminOverviewStatsResponseDTO;

public interface AdminOverviewService {
    AdminOverviewStatsResponseDTO getOverviewStats();
}
