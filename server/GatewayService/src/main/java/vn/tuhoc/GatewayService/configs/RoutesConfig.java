package vn.tuhoc.GatewayService.configs;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutesConfig {
        private record ServiceRoute(
                        String routeId,
                        String serviceName,
                        String path) {
        }

        private static final List<ServiceRoute> ROUTES = List.of(
                        new ServiceRoute(
                                        "auth-route",
                                        "user-service",
                                        "/api/v1/auth/**"),
                        new ServiceRoute(
                                        "direct-message-route",
                                        "relationship-service",
                                        "/api/v1/users/*/messages"),
                        new ServiceRoute(
                                        "user-route",
                                        "user-service",
                                        "/api/v1/users/**"),
                        new ServiceRoute(
                                        "relationship-route",
                                        "relationship-service",
                                        "/api/v1/relationships/**"),
                        new ServiceRoute(
                                        "conversation-route",
                                        "relationship-service",
                                        "/api/v1/conversations/**"),
                        new ServiceRoute(
                                        "post-route",
                                        "content-service",
                                        "/api/v1/posts/**"),
                        new ServiceRoute(
                                        "story-route",
                                        "content-service",
                                        "/api/v1/stories/**"),
                        new ServiceRoute(
                                        "interaction-route",
                                        "interaction-service",
                                        "/api/v1/interactions/**"),
                        new ServiceRoute(
                                        "admin-post-route",
                                        "content-service",
                                        "/api/v1/admin/posts/**"),
                        new ServiceRoute(
                                        "admin-story-route",
                                        "content-service",
                                        "/api/v1/admin/stories/**"),
                        new ServiceRoute(
                                        "admin-overview-route",
                                        "content-service",
                                        "/api/v1/admin/overview/**"),
                        new ServiceRoute(
                                        "media-route",
                                        "media-service",
                                        "/api/v1/medias/**"),
                        new ServiceRoute(
                                        "notification-route",
                                        "notification-service",
                                        "/api/v1/notifications/**"));

        private void serviceRoute(
                        RouteLocatorBuilder.Builder routes,
                        ServiceRoute serviceRoute) {
                routes.route(serviceRoute.routeId(), route -> route
                                .path(serviceRoute.path())
                                .uri("lb://" + serviceRoute.serviceName()));
        }

        private void openApiRoute(
                        RouteLocatorBuilder.Builder routes,
                        String serviceName) {
                String openApiId = String.format("%s-openapi", serviceName);
                String openApiPath = String.format("/v3/api-docs/%s", serviceName);
                String loadBalancerPath = String.format("lb://%s", serviceName);

                routes.route(openApiId, route -> route
                                .path(openApiPath)
                                .filters(filter -> filter.rewritePath(openApiPath, "/v3/api-docs"))
                                .uri(loadBalancerPath));
        }

        @Bean
        public RouteLocator serviceRoutes(RouteLocatorBuilder builder) {
                RouteLocatorBuilder.Builder routes = builder.routes();
                Set<String> openApiServices = new HashSet<>();

                for (ServiceRoute serviceRoute : ROUTES) {
                        this.serviceRoute(routes, serviceRoute);
                        if (openApiServices.add(serviceRoute.serviceName())) {
                                this.openApiRoute(routes, serviceRoute.serviceName());
                        }
                }

                return routes.build();
        }
}
