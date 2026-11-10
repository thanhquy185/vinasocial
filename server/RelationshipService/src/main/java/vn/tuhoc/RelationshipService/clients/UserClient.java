package vn.tuhoc.RelationshipService.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import vn.tuhoc.RelationshipService.dtos.clients.UserProfileRelationshipResClientDTO;
import vn.tuhoc.core.dtos.responses.RestResponseDTO;

@FeignClient(name = "user-service")
public interface UserClient {
    @GetMapping("/api/v1/users/profile/{username}")
    RestResponseDTO<UserProfileRelationshipResClientDTO> getPublicProfile(@PathVariable("username") String username);
}
