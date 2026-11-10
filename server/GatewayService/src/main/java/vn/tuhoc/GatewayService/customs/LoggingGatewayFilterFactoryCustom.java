package vn.tuhoc.GatewayService.customs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component("Logging")
public class LoggingGatewayFilterFactoryCustom
        extends AbstractGatewayFilterFactory<LoggingGatewayFilterFactoryCustom.Config> {
    private static final Logger logger = LoggerFactory.getLogger(LoggingGatewayFilterFactoryCustom.class);

    public LoggingGatewayFilterFactoryCustom() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            logger.info("Pre Gateway Filter");

            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {
                        logger.info("Post Gateway Filter");
                    }));
        };
    }

    public static class Config {
        private String baseMsg;

        public void setBaseMsg(String baseMsg) {
            this.baseMsg = baseMsg;
        }

        public String getBaseMsg() {
            return this.baseMsg;
        }
    }
}