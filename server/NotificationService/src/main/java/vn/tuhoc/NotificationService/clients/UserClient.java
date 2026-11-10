package vn.tuhoc.NotificationService.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import vn.tuhoc.core.dtos.clients.UserInfoReqClientDTO;
import vn.tuhoc.core.dtos.clients.UserInfoResClientDTO;

@FeignClient(name = "user-service", fallbackFactory = NotificationUserClientFallbackFactory.class)
public interface UserClient {
    @PostMapping("/api/v1/users/info")
    List<UserInfoResClientDTO> getInfo(@RequestBody UserInfoReqClientDTO request);
}

@Component
class NotificationUserClientFallbackFactory implements FallbackFactory<UserClient> {
    @Override
    public UserClient create(Throwable cause) {
        return request -> List.of();
    }
}
