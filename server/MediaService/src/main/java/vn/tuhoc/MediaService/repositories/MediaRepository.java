package vn.tuhoc.MediaService.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import vn.tuhoc.MediaService.domains.entities.MediaEntity;
import vn.tuhoc.MediaService.domains.enums.MediaPurposeEnum;
import vn.tuhoc.MediaService.domains.enums.MediaStatusEnum;

public interface MediaRepository extends MongoRepository<MediaEntity, String> {
    List<MediaEntity> findAllByIdInAndUserIdAndPurposeAndStatus(
            List<String> ids,
            Integer userId,
            MediaPurposeEnum purpose,
            MediaStatusEnum status);

    @Query(value = "{ '_id': { $in: ?0 } }", fields = """
            {
                '_id': 1,
                'type': 1,
                'purpose': 1,
                'objectKey': 1,
                'originalFilename': 1,
                'contentType': 1,
                'width': 1,
                'height': 1
            }
            """)
    List<MediaEntity> findMediaInfoByIds(List<String> ids);
}
