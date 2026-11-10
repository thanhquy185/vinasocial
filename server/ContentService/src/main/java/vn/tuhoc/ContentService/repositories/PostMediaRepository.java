package vn.tuhoc.ContentService.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.tuhoc.ContentService.domains.entities.PostMediaEntity;

public interface PostMediaRepository
                extends JpaRepository<PostMediaEntity, Integer>, JpaSpecificationExecutor<PostMediaEntity> {
        Optional<PostMediaEntity> findByPostId(Integer postId);

        Optional<PostMediaEntity> findByMediaId(String mediaId);
}
