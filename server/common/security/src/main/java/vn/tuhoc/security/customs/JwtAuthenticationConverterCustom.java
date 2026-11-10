package vn.tuhoc.security.customs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationConverterCustom implements Converter<Jwt, AbstractAuthenticationToken> {
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Map<String, Object> user = jwt.getClaim("user");
        List<GrantedAuthority> authorities = new ArrayList<>();

        String role = (String) user.get("role");
        if (role != null) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        @SuppressWarnings("unchecked")
        List<String> authorityClaims = (List<String>) user.get("authorities");
        if (authorityClaims != null) {
            authorityClaims.forEach(authority -> authorities.add(new SimpleGrantedAuthority(authority)));
        }

        return new JwtAuthenticationToken(jwt, authorities);
    }
}
