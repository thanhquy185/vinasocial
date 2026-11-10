package vn.tuhoc.ContentService.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import vn.tuhoc.ContentService.dtos.clients.UserProfileVisibilityResClientDTO;
import vn.tuhoc.core.dtos.clients.UserInfoReqClientDTO;
import vn.tuhoc.core.dtos.clients.UserInfoResClientDTO;
import vn.tuhoc.core.dtos.responses.RestResponseDTO;

@FeignClient(name = "user-service", fallbackFactory = UserClientFallbackFactory.class)
public interface UserClient {
    @PostMapping("/api/v1/users/info")
    List<UserInfoResClientDTO> getInfo(@RequestBody UserInfoReqClientDTO userInfoReqClientDTO);

    @GetMapping("/api/v1/users/profile/{username}")
    RestResponseDTO<UserProfileVisibilityResClientDTO> getPublicProfile(@PathVariable("username") String username);
}

@Component
class UserClientFallbackFactory implements FallbackFactory<UserClient> {
    @Override
    public UserClient create(Throwable cause) {
        return new UserClient() {
            @Override
            public List<UserInfoResClientDTO> getInfo(UserInfoReqClientDTO userInfoReqClientDTO) {
                System.out.println("User Service lỗi: " + cause.getMessage());

                return List.of();
            }

            @Override
            public RestResponseDTO<UserProfileVisibilityResClientDTO> getPublicProfile(String username) {
                System.out.println("User Service lỗi: " + cause.getMessage());
                return null;
            }
        };
    }
}
