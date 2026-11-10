package vn.tuhoc.ContentService.configs;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic notificationCreatedTopic() {
        return TopicBuilder.name("notification_created").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic postPublishedTopic() {
        return TopicBuilder.name("post_published").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic postUpdatedTopic() {
        return TopicBuilder.name("post_updated").partitions(1).replicas(1).build();
    }
}
