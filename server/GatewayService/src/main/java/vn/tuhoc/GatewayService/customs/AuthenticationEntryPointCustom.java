package vn.tuhoc.GatewayService.customs;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import vn.tuhoc.core.dtos.responses.RestResponseDTO;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthenticationEntryPointCustom implements ServerAuthenticationEntryPoint {
        private final ObjectMapper objectMapper;

        @Override
        public Mono<Void> commence(
                        ServerWebExchange serverWebExchange,
                        AuthenticationException authenticationException) {
                serverWebExchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                serverWebExchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

                String error = Optional.ofNullable(authenticationException.getCause())
                                .map(Throwable::getMessage)
                                .orElse(authenticationException.getMessage());

                RestResponseDTO<String> restResponseDTO = RestResponseDTO.<String>builder()
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .error(error)
                                .message("Token không hợp lệ hoặc chưa được cung cấp!")
                                .data(serverWebExchange.getRequest().getPath().value())
                                .build();

                try {
                        byte[] bytes = this.objectMapper.writeValueAsString(restResponseDTO)
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
