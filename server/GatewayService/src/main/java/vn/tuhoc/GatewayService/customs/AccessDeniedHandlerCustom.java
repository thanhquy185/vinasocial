package vn.tuhoc.GatewayService.customs;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import vn.tuhoc.core.dtos.responses.RestResponseDTO;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AccessDeniedHandlerCustom implements ServerAccessDeniedHandler {
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(
            ServerWebExchange serverWebExchange,
            AccessDeniedException accessDeniedException) {
        serverWebExchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        serverWebExchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String error = Optional.ofNullable(accessDeniedException.getCause())
                .map(Throwable::getMessage)
                .orElse(accessDeniedException.getMessage());

        RestResponseDTO<Object> restResponseDTO = RestResponseDTO.<Object>builder()
                .status(HttpStatus.FORBIDDEN.value())
                .error(error)
                .message("Bạn không có quyền truy cập tài nguyên này!")
                .build();

        try {
            byte[] bytes = objectMapper.writeValueAsString(restResponseDTO)
                    .getBytes(StandardCharsets.UTF_8);

            return serverWebExchange.getResponse()
                    .writeWith(Mono.just(
                            serverWebExchange.getResponse()
                                    .bufferFactory()
                                    .wrap(bytes)));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}