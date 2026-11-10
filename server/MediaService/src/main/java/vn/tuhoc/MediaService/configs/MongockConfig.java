package vn.tuhoc.MediaService.configs;

import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.mongodb.springdata.v4.SpringDataMongoV4Driver; // Thay đổi v4/v2 tùy dependency bạn dùng
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongockConfig {
    @Bean
    public ConnectionDriver connectionDriver(MongoTemplate mongoTemplate) {
        // Tạo driver dựa trên MongoTemplate của Spring
        SpringDataMongoV4Driver driver = SpringDataMongoV4Driver.withDefaultLock(mongoTemplate);

        // (Tùy chọn) Cấu hình disable giao dịch nếu MongoDB của bạn là Standalone
        // (không phải Replica Set)
        driver.disableTransaction();

        return driver;
    }
}
