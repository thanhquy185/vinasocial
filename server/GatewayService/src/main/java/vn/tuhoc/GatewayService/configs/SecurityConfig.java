package vn.tuhoc.GatewayService.configs;

import vn.tuhoc.GatewayService.customs.AccessDeniedHandlerCustom;
import vn.tuhoc.GatewayService.customs.AuthenticationEntryPointCustom;
import vn.tuhoc.GatewayService.customs.AuthorizeExchangeCustom;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.nimbusds.jose.util.Base64;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Configuration
@EnableWebFluxSecurity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SecurityConfig {
        static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;
        @Value("${jwt.base64-secret}")
        String jwtKey;

        @Bean
        public SecretKey jwtSecretKey() {
                byte[] keyBytes = Base64.from(jwtKey).decode();

                return new SecretKeySpec(
                                keyBytes,
                                JWT_ALGORITHM.getName());
        }

        @Bean
        public ReactiveJwtDecoder jwtDecoder(SecretKey jwtSecretKey) {
                return NimbusReactiveJwtDecoder
                                .withSecretKey(jwtSecretKey)
                                .macAlgorithm(JWT_ALGORITHM)
                                .build();
        }

        @Bean
        public SecurityWebFilterChain securityWebFilterChain(
                        ServerHttpSecurity http,
                        AuthorizeExchangeCustom authorizeExchangeCustom,
                        AuthenticationEntryPointCustom authenticationEntryPointCustom,
                        AccessDeniedHandlerCustom accessDeniedHandlerCustom) {
                return http
                                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                                .cors(Customizer.withDefaults())

                                .authorizeExchange(auth -> authorizeExchangeCustom.configure(auth))

                                .oauth2ResourceServer(oauth2 -> oauth2
                                                .jwt(Customizer.withDefaults())
                                                .authenticationEntryPoint(authenticationEntryPointCustom)
                                                .accessDeniedHandler(accessDeniedHandlerCustom))

                                .build();
        }
}
