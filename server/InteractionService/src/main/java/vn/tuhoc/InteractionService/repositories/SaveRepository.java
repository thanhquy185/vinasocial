package vn.tuhoc.InteractionService.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.tuhoc.InteractionService.domains.entities.SaveEntity;

public interface SaveRepository extends MongoRepository<SaveEntity, Integer> {
    Optional<SaveEntity> findByUserIdAndPostId(Integer userId, Integer postId);

    boolean existsByUserIdAndPostId(Integer userId, Integer postId);

    Page<SaveEntity> findAllByUserId(Integer userId, Pageable pageable);

    long countByPostId(Integer postId);

    void deleteByUserIdAndPostId(Integer userId, Integer postId);
}
