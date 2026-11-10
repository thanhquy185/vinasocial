// package vn.tuhoc.UserService.customs;

// import java.io.IOException;

// import javax.crypto.SecretKey;
// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import lombok.AccessLevel;
// import lombok.RequiredArgsConstructor;
// import lombok.experimental.FieldDefaults;
// import vn.tuhoc.core.utils.ValidationUtil;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
// import org.springframework.security.oauth2.jwt.Jwt;
// import org.springframework.security.oauth2.jwt.JwtDecoder;
// import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
// import org.springframework.stereotype.Component;
// import org.springframework.web.filter.OncePerRequestFilter;

// import com.nimbusds.jose.util.Base64;

// @Component
// @RequiredArgsConstructor
// @FieldDefaults(level = AccessLevel.PRIVATE)
// public class JwtAuthenticationFilterCustom extends OncePerRequestFilter {
//     @Value("${jwt.base64-secret}")
//     String jwtKey;
//     final UserDetailsServiceCustom userDetailsService;

//     private String getToken(HttpServletRequest request) {
//         String header = request.getHeader("Authorization");
//         if (ValidationUtil.nonNull(header) && header.startsWith("Bearer ")) {
//             return header.substring(7);
//         }

//         return null;
//     }

//     private JwtDecoder getDecoder() {
//         byte[] keyBytes = Base64.from(jwtKey).decode();
//         SecretKey secretKey = new javax.crypto.spec.SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA512");

//         return NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS512).build();
//     }

//     @Override
//     protected void doFilterInternal(
//             HttpServletRequest request,
//             HttpServletResponse response,
//             FilterChain filterChain) throws ServletException, IOException {
//         try {
//             String token = this.getToken(request);
//             if (ValidationUtil.nonNull(token)) {
//                 Jwt jwt = this.getDecoder().decode(token);
//                 String username = jwt.getSubject();

//                 UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
//                 UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                         userDetails, null, userDetails.getAuthorities());

//                 SecurityContextHolder.getContext().setAuthentication(authentication);
//             }
//         } catch (Exception e) {
//             // Token không hợp lệ → Không set authentication
//             logger.warn("JWT filter error: " + e.getMessage());
//         }

//         filterChain.doFilter(request, response);
//     }
// }
