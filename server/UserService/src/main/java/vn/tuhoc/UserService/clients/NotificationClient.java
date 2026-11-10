package vn.tuhoc.UserService.clients;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import vn.tuhoc.UserService.dtos.requests.UserTestConnectRequestDTO;

@FeignClient(name = "notification-service", fallbackFactory = NotificationClientFallbackFactory.class)
public interface NotificationClient {
    @PostMapping("/api/v1/notifications/notification-test-connect")
    public void notificationTestConnect(@RequestBody UserTestConnectRequestDTO userTestConnect);
}

@Component
class NotificationClientFallbackFactory implements FallbackFactory<NotificationClient> {
    @Override
    public NotificationClient create(Throwable cause) {
        return new NotificationClient() {
            @Override
            public void notificationTestConnect(UserTestConnectRequestDTO userTestConnect) {
                System.out.println("Notification Service lỗi: " + cause.getMessage());
            }
        };
    }
}
