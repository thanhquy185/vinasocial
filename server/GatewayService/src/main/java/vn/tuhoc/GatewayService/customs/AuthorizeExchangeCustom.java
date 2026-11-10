package vn.tuhoc.GatewayService.customs;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec;
import org.springframework.stereotype.Component;

@Component
public class AuthorizeExchangeCustom {
    public void configure(AuthorizeExchangeSpec auth) {
        auth.pathMatchers(HttpMethod.OPTIONS, "/**").permitAll();

        auth.pathMatchers("/api/v1/auth/password/**").authenticated();
        auth.pathMatchers("/api/v1/auth/**").permitAll();

        auth.pathMatchers(HttpMethod.GET, "/api/v1/users/profile/**").permitAll();
        auth.pathMatchers(HttpMethod.GET, "/api/v1/posts/profile/**").permitAll();

        auth.anyExchange().authenticated();
    }
}
