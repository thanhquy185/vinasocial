package vn.tuhoc.security.customs;

import java.io.IOException;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import vn.tuhoc.core.dtos.responses.RestResponseDTO;

@Component
@RequiredArgsConstructor
public class AuthenticationEntryPointCustom implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authenticationException) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");

        String error = Optional.ofNullable(authenticationException.getCause())
                .map(Throwable::getMessage)
                .orElse(authenticationException.getMessage());

        String message = "Token không hợp lệ hoặc chưa được cung cấp!";
        if (authenticationException instanceof BadCredentialsException) {
            message = "Tên tài khoản hoặc mật khẩu không đúng!";
        }

        RestResponseDTO<Object> restResponseDTO = RestResponseDTO.<Object>builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(error)
                .message(message)
                .build();

        this.objectMapper.writeValue(response.getWriter(), restResponseDTO);
    }
}
