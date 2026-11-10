package vn.tuhoc.NotificationService.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.security.customs.AccessDeniedHandlerCustom;
import vn.tuhoc.security.customs.AuthenticationEntryPointCustom;
import vn.tuhoc.security.customs.BearerTokenResolverCustom;
import vn.tuhoc.security.customs.JwtAuthenticationConverterCustom;
import vn.tuhoc.security.utils.SecurityUtil;

@Configuration
@EnableWebSecurity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SecurityConfig {
    @Value("${jwt.base64-secret}")
    String jwtKey;

    @Bean
    public JwtDecoder jwtDecoder() {
        return SecurityUtil.getJwtDecoder(this.jwtKey);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            BearerTokenResolverCustom bearerTokenResolverCustom,
            JwtAuthenticationConverterCustom jwtAuthenticationConverterCustom,
            AuthenticationEntryPointCustom authenticationEntryPointCustom,
            AccessDeniedHandlerCustom accessDeniedHandlerCustom) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/notifications/notification-test-connect").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPointCustom)
                        .accessDeniedHandler(accessDeniedHandlerCustom))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverterCustom))
                        .bearerTokenResolver(bearerTokenResolverCustom)
                        .authenticationEntryPoint(authenticationEntryPointCustom)
                        .accessDeniedHandler(accessDeniedHandlerCustom))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
