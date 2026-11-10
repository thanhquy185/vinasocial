package vn.tuhoc.security.utils;

import java.util.Map;
import java.util.Optional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import com.nimbusds.jose.util.Base64;

public class SecurityUtil {
    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        } else if (authentication.getPrincipal() instanceof String s) {
            return s;
        }

        return null;
    }

    public static SecretKey getSecretKey(String jwtKey) {
        byte[] keyBytes = Base64.from(jwtKey).decode();

        return new SecretKeySpec(keyBytes, 0, keyBytes.length, SecurityUtil.JWT_ALGORITHM.getName());
    }

    public static JwtDecoder getJwtDecoder(String jwtKey) {
        // return NimbusJwtDecoder
        // .withSecretKey(SecurityUtil.getSecretKey(jwtKey))
        // .macAlgorithm(SecurityUtil.JWT_ALGORITHM)
        // .build();

        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
                .withSecretKey(SecurityUtil.getSecretKey(jwtKey))
                .macAlgorithm(SecurityUtil.JWT_ALGORITHM)
                .build();

        return token -> {
            try {
                return jwtDecoder.decode(token);
            } catch (Exception e) {
                System.out.println(">>> JWT error: " + e.getMessage());
                throw e;
            }
        };
    }

    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        String extractPrincipal = SecurityUtil.extractPrincipal(securityContext.getAuthentication());

        return Optional.ofNullable(extractPrincipal);
    }

    public static Optional<Integer> getCurrentUserId() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null) {
            return Optional.empty();
        }
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            Map<String, Object> user = jwt.getClaim("user");
            if (user == null) {
                return Optional.empty();
            }

            Object id = user.get("id");
            if (id instanceof Number number) {
                return Optional.of(number.intValue());
            }
        }

        return Optional.empty();
    }

}
