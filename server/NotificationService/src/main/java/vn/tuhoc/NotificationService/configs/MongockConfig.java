package vn.tuhoc.NotificationService.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.mongodb.springdata.v4.SpringDataMongoV4Driver;

@Configuration
public class MongockConfig {
    @Bean
    public ConnectionDriver connectionDriver(MongoTemplate mongoTemplate) {
        SpringDataMongoV4Driver driver = SpringDataMongoV4Driver.withDefaultLock(mongoTemplate);
        driver.disableTransaction();
        return driver;
    }
}
