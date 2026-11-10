package vn.tuhoc.UserService.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import vn.tuhoc.core.dtos.clients.MediaInfoReqClientDTO;
import vn.tuhoc.core.dtos.clients.MediaInfoResClientDTO;
import vn.tuhoc.UserService.dtos.clients.MediaValidReqClientDTO;

@FeignClient(name = "media-service", fallbackFactory = MediaClientFallbackFactory.class)
public interface MediaClient {
    @PostMapping("/api/v1/medias/info")
    List<MediaInfoResClientDTO> getInfo(@RequestBody MediaInfoReqClientDTO mediaInfoReqClientDTO);

    @PostMapping("/api/v1/medias/validate")
    Boolean validateMedia(@RequestBody MediaValidReqClientDTO mediaValidReqClientDTO);
}

@Component
class MediaClientFallbackFactory implements FallbackFactory<MediaClient> {
    @Override
    public MediaClient create(Throwable cause) {
        return new MediaClient() {
            @Override
            public List<MediaInfoResClientDTO> getInfo(MediaInfoReqClientDTO mediaInfoReqClientDTO) {
                System.out.println("Media Service lỗi: " + cause.getMessage());

                return List.of();
            }

            @Override
            public Boolean validateMedia(MediaValidReqClientDTO mediaValidReqClientDTO) {
                System.out.println("Media Service lỗi: " + cause.getMessage());
                return false;
            }
        };
    }
}
