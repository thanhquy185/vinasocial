package vn.tuhoc.GatewayService.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Configuration
public class GlobalFilterConfig implements GlobalFilter, Ordered {
    final Logger logger = LoggerFactory.getLogger(GlobalFilterConfig.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // String queryParamLocale = exchange.getRequest()
        // .getQueryParams()
        // .getFirst("locale");

        ServerWebExchange modifiedExchange = exchange.mutate()
                .request(originalRequest -> originalRequest
                        .headers(httpHeaders -> httpHeaders.remove(HttpHeaders.ORIGIN)))
                .build();

        logger.info("Pre Global Filter");
        return chain.filter(modifiedExchange)
                .then(Mono.fromRunnable(() -> {
                    logger.info("Post Global Filter");
                }));
    }

    @Override
    public int getOrder() {
        return -1;
    }

}
