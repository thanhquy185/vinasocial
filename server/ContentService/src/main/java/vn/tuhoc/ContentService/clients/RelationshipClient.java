package vn.tuhoc.ContentService.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import vn.tuhoc.core.dtos.responses.RestResponseDTO;

@FeignClient(name = "relationship-service", fallbackFactory = RelationshipClientFallbackFactory.class)
public interface RelationshipClient {
    @PostMapping("/api/v1/relationships/me/story-audience/{visibility}")
    RestResponseDTO<List<Integer>> getStoryAudience(
            @PathVariable("visibility") String visibility,
            @RequestBody List<Integer> ownerUserIds);
}

@Component
class RelationshipClientFallbackFactory implements FallbackFactory<RelationshipClient> {
    @Override
    public RelationshipClient create(Throwable cause) {
        return (visibility, ownerUserIds) -> null;
    }
}
