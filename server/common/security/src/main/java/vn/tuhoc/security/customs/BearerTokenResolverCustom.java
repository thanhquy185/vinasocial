package vn.tuhoc.security.customs;

import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class BearerTokenResolverCustom implements BearerTokenResolver {
    private final BearerTokenResolver delegate = new DefaultBearerTokenResolver();

    @Override
    public String resolve(HttpServletRequest request) {
        String path = request.getServletPath();
        boolean isAuthenticatedAuthEndpoint = path.equals("/api/v1/auth/mfa")
                || path.startsWith("/api/v1/auth/mfa/")
                || path.startsWith("/api/v1/auth/password/")
                || path.equals("/api/v1/auth/sessions")
                || path.startsWith("/api/v1/auth/sessions/");
        if ((path.startsWith("/api/v1/auth/") && !isAuthenticatedAuthEndpoint)
                || path.startsWith("/oauth2/")
                || path.startsWith("/login/oauth2/")) {
            return null;
        }

        return this.delegate.resolve(request);
    }
}
