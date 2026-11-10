package vn.tuhoc.ContentService.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
// import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
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
@EnableMethodSecurity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SecurityConfig {
        @Value("${jwt.base64-secret}")
        String jwtKey;

        @Bean
        public JwtDecoder jwtDecoder() {
                // NimbusJwtDecoder jwtDecoder = SecurityUtil.getJwtDecoder(this.jwtKey);

                // return token -> {
                // try {
                // return jwtDecoder.decode(token);
                // } catch (Exception e) {
                // System.out.println(">>> JWT error: " + e.getMessage());
                // throw e;
                // }
                // };

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
                                                .requestMatchers("/api/v1/posts/**").permitAll()
                                                .anyRequest().authenticated())

                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint(authenticationEntryPointCustom)
                                                .accessDeniedHandler(accessDeniedHandlerCustom))

                                .oauth2ResourceServer(oauth2 -> oauth2
                                                .jwt(jwt -> jwt.jwtAuthenticationConverter(
                                                                jwtAuthenticationConverterCustom))
                                                .bearerTokenResolver(bearerTokenResolverCustom)
                                                .authenticationEntryPoint(authenticationEntryPointCustom)
                                                .accessDeniedHandler(accessDeniedHandlerCustom))

                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

                return http.build();
        }
}