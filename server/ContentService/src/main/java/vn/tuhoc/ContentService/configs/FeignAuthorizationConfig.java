package vn.tuhoc.ContentService.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import vn.tuhoc.core.utils.ValidationUtil;

@Configuration
public class FeignAuthorizationConfig {
    @Bean
    RequestInterceptor forwardAuthorizationHeader() {
        return template -> {
            if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
                HttpServletRequest request = attributes.getRequest();
                String authorization = request.getHeader("Authorization");
                if (ValidationUtil.nonNull(authorization) && !authorization.isBlank()) {
                    template.header("Authorization", authorization);
                }
            }
        };
    }
}
