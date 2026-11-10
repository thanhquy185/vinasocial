package vn.tuhoc.InteractionService.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.tuhoc.InteractionService.domains.entities.ReupEntity;

public interface ReupRepository extends MongoRepository<ReupEntity, Integer> {
    Optional<ReupEntity> findByUserIdAndPostId(Integer userId, Integer postId);

    boolean existsByUserIdAndPostId(Integer userId, Integer postId);

    Page<ReupEntity> findAllByUserId(Integer userId, Pageable pageable);

    Page<ReupEntity> findAllByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);

    long countByPostId(Integer postId);

    void deleteByUserIdAndPostId(Integer userId, Integer postId);
}
