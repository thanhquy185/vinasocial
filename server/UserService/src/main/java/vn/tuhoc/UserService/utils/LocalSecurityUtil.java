package vn.tuhoc.UserService.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
// import java.util.List;
import java.util.Map;
// import java.util.Optional;

// import javax.crypto.SecretKey;
// import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContext;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
// import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
// import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;

// import com.nimbusds.jose.util.Base64;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
// import vn.tuhoc.UserService.domains.enums.UserRoleEnum;
import vn.tuhoc.UserService.dtos.responses.AuthLoginResponseDTO;
import vn.tuhoc.security.utils.SecurityUtil;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocalSecurityUtil {
    // public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

    @Value("${jwt.base64-secret}")
    @NonFinal
    String jwtKey;
    @Value("${jwt.access-token-validity-in-seconds}")
    @NonFinal
    Long jwtAccessTokenExpiration;
    @Value("${jwt.refresh-token-validity-in-seconds}")
    @NonFinal
    Long jwtRefreshTokenExpiration;
    JwtEncoder jwtEncoder;
    // final JwtAuthorityUtil jwtAuthorityUtil;

    private Map<String, Object> getUserClams(
            Boolean isAccessToken,
            String username,
            AuthLoginResponseDTO restLoginDTO) {
        // UserRoleEnum userRoleEnum = restLoginDTO.getUser().getRole();

        Map<String, Object> userClaims = new HashMap<>();
        userClaims.put("id", restLoginDTO.getUserLogin().getId());
        userClaims.put("publicId", restLoginDTO.getUserLogin().getPublicId());
        userClaims.put("role", String.format("ROLE_%s", restLoginDTO.getUserLogin().getRole().getValue()));
        // if (isAccessToken
        // && (userRoleEnum.equals(UserRoleEnum.MANAGER) ||
        // userRoleEnum.equals(UserRoleEnum.EMPLOYEE))) {
        // List<String> authorities = this.jwtAuthorityUtil.generateAuthorities(
        // userRoleEnum.equals(UserRoleEnum.MANAGER),
        // restLoginDTO.getUserLogin().getId());

        // userClaims.put("authorities", authorities);
        // }
        userClaims.put("username", username);
        userClaims.put("status", restLoginDTO.getUserLogin().getStatus().getDescription());
        userClaims.put("sessionDeviceId", restLoginDTO.getSessionDeviceId());
        userClaims.put("installationId", restLoginDTO.getInstallationId());

        return userClaims;
    }

    // private String extractPrincipal(Authentication authentication) {
    // if (authentication == null) {
    // return null;
    // } else if (authentication.getPrincipal() instanceof UserDetails
    // springSecurityUser) {
    // return springSecurityUser.getUsername();
    // } else if (authentication.getPrincipal() instanceof Jwt jwt) {
    // return jwt.getSubject();
    // } else if (authentication.getPrincipal() instanceof String s) {
    // return s;
    // }

    // return null;
    // }

    private JwtClaimsSet generateClaims(
            Boolean isAccessToken,
            Instant now,
            Instant validity,
            String username,
            AuthLoginResponseDTO restLoginDTO) {
        return JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(username)
                .claim("user", this.getUserClams(isAccessToken, username, restLoginDTO))
                .build();
    }

    // private SecretKey getSecretKey() {
    // byte[] keyBytes = Base64.from(this.jwtKey).decode();

    // return new SecretKeySpec(keyBytes, 0, keyBytes.length,
    // SecurityUtil.JWT_ALGORITHM.getName());
    // }

    private String generateToken(String type, String username, AuthLoginResponseDTO restLoginDTO) {
        boolean isAccessToken = type.equalsIgnoreCase("access-token");

        Instant now = Instant.now();
        Instant validity = now.plus(
                isAccessToken ? this.jwtAccessTokenExpiration : this.jwtRefreshTokenExpiration,
                ChronoUnit.SECONDS);

        JwsHeader jwsHeader = JwsHeader.with(SecurityUtil.JWT_ALGORITHM).build();

        JwtClaimsSet claims = this.generateClaims(isAccessToken, now, validity, username, restLoginDTO);

        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    // public Optional<String> getCurrentUserLogin() {
    // SecurityContext securityContext = SecurityContextHolder.getContext();
    // String extractPrincipal =
    // this.extractPrincipal(securityContext.getAuthentication());

    // return Optional.ofNullable(extractPrincipal);
    // }

    public String createAccessToken(String username, AuthLoginResponseDTO restLoginDTO) {
        return this.generateToken("access-token", username, restLoginDTO);
    }

    public String createRefreshToken(String username, AuthLoginResponseDTO restLoginDTO) {
        return this.generateToken("refresh-token", username, restLoginDTO);
    }

    public String getCurrentSessionDeviceId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return null;
        }

        Object userClaim = jwt.getClaims().get("user");
        if (!(userClaim instanceof Map<?, ?> user)) return null;
        Object sessionDeviceId = user.get("sessionDeviceId");
        return sessionDeviceId instanceof String value ? value : null;
    }

    // public Jwt checkValidRefreshToken(String token) {
    // NimbusJwtDecoder jwtDecoder = SecurityUtil.getJwtDecoder(this.jwtKey);

    // try {
    // return jwtDecoder.decode(token);
    // } catch (Exception e) {
    // System.out.println(">>> JWT error: " + e.getMessage());
    // throw e;
    // }
    // }
}
