package vn.tuhoc.RelationshipService.configs;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import lombok.RequiredArgsConstructor;
import vn.tuhoc.RelationshipService.dtos.events.PostPublishedEventDTO;
import vn.tuhoc.RelationshipService.dtos.events.PostUpdatedEventDTO;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {
    private final KafkaProperties kafkaProperties;

    @Bean
    public ConsumerFactory<String, PostPublishedEventDTO> postPublishedConsumerFactory() {
        Map<String, Object> properties = new HashMap<>(this.kafkaProperties.buildConsumerProperties());
        return new DefaultKafkaConsumerFactory<>(
                properties,
                new StringDeserializer(),
                new JacksonJsonDeserializer<>(PostPublishedEventDTO.class, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PostPublishedEventDTO> postPublishedTopicListenerFactory(
            ConsumerFactory<String, PostPublishedEventDTO> postPublishedConsumerFactory) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, PostPublishedEventDTO>();
        factory.setConsumerFactory(postPublishedConsumerFactory);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, PostUpdatedEventDTO> postUpdatedConsumerFactory() {
        Map<String, Object> properties = new HashMap<>(this.kafkaProperties.buildConsumerProperties());
        return new DefaultKafkaConsumerFactory<>(
                properties,
                new StringDeserializer(),
                new JacksonJsonDeserializer<>(PostUpdatedEventDTO.class, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PostUpdatedEventDTO> postUpdatedTopicListenerFactory(
            ConsumerFactory<String, PostUpdatedEventDTO> postUpdatedConsumerFactory) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, PostUpdatedEventDTO>();
        factory.setConsumerFactory(postUpdatedConsumerFactory);
        return factory;
    }
}
