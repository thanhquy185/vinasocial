package vn.tuhoc.InteractionService.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.tuhoc.InteractionService.domains.entities.ReportEntity;
import vn.tuhoc.InteractionService.domains.enums.ReportStatusEnum;

public interface ReportRepository extends MongoRepository<ReportEntity, Integer> {
    Page<ReportEntity> findAllByUserId(Integer userId, Pageable pageable);

    Page<ReportEntity> findAllByStatus(ReportStatusEnum status, Pageable pageable);
}
