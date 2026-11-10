package vn.tuhoc.UserService.configs;

// import javax.crypto.SecretKey;
// import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
// import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
// import com.nimbusds.jose.util.Base64;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
// import vn.tuhoc.UserService.customs.JwtAuthenticationFilterCustom;
import vn.tuhoc.UserService.customs.UserDetailsServiceCustom;
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
        final UserDetailsServiceCustom userDetailsServiceCustom;

        SecurityConfig(UserDetailsServiceCustom userDetailsServiceCustom) {
                this.userDetailsServiceCustom = userDetailsServiceCustom;
        }

        // private SecretKey getSecretKey() {
        // byte[] keyBytes = Base64.from(this.jwtKey).decode();

        // return new SecretKeySpec(keyBytes, 0, keyBytes.length,
        // SecurityUtil.JWT_ALGORITHM.getName());
        // }

        @Bean
        public JwtEncoder jwtEncoder() {
                // return new NimbusJwtEncoder(new ImmutableSecret<>(this.getSecretKey()));

                return new NimbusJwtEncoder(new ImmutableSecret<>(SecurityUtil.getSecretKey(this.jwtKey)));
        }

        @Bean
        public JwtDecoder jwtDecoder() {
                // NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
                // this.getSecretKey()).macAlgorithm(SecurityUtil.JWT_ALGORITHM).build();

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
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public DaoAuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider provider = new DaoAuthenticationProvider(this.userDetailsServiceCustom);
                provider.setPasswordEncoder(this.passwordEncoder());

                return provider;
        }

        @Bean
        public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
                return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
                                .authenticationProvider(this.authenticationProvider())
                                .build();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http,
                        // JwtAuthenticationFilterCustom jwtAuthenticationFilterCustom,
                        BearerTokenResolverCustom bearerTokenResolverCustom,
                        AuthenticationEntryPointCustom authenticationEntryPointCustom,
                        AccessDeniedHandlerCustom accessDeniedHandlerCustom,
                        JwtAuthenticationConverterCustom jwtAuthenticationConverterCustom) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())

                                .cors(Customizer.withDefaults())

                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/api/v1/auth/password/**").authenticated()
                                                .requestMatchers("/api/v1/auth/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/v1/users/profile/**").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/v1/users/info").permitAll()
                                                .anyRequest().authenticated())

                                // .addFilterBefore(jwtAuthenticationFilterCustom,
                                // UsernamePasswordAuthenticationFilter.class)

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
