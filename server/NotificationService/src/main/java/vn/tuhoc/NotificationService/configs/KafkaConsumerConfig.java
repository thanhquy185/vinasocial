package vn.tuhoc.NotificationService.configs;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import vn.tuhoc.NotificationService.dtos.events.AuthOtpForgotPasswordByEmailEventDTO;
import vn.tuhoc.NotificationService.dtos.events.AuthOtpForgotPasswordByPhoneEventDTO;
import vn.tuhoc.NotificationService.dtos.events.AuthOtpRegisterByEmailEventDTO;
import vn.tuhoc.NotificationService.dtos.events.AuthOtpRegisterByPhoneEventDTO;
import vn.tuhoc.NotificationService.dtos.events.AuthOtpSecurityEventDTO;
import vn.tuhoc.NotificationService.dtos.events.NotificationCreatedEventDTO;
import vn.tuhoc.NotificationService.dtos.events.NotificationPreferencesUpdatedEventDTO;
import vn.tuhoc.NotificationService.dtos.events.UserCreatedEventDTO;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KafkaConsumerConfig {
    final KafkaProperties kafkaProperties;

    private <T> ConsumerFactory<String, T> createConsumerFactory(Class<T> eventType) {
        Map<String, Object> props = new HashMap<>(this.kafkaProperties.buildConsumerProperties());

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JacksonJsonDeserializer<>(eventType, false));
    }

    private <T> ConcurrentKafkaListenerContainerFactory<String, T> createListenerFactory(
            Class<T> eventType,
            DefaultErrorHandler defaultErrorHandler) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, T>();
        factory.setConsumerFactory(this.createConsumerFactory(eventType));
        factory.setCommonErrorHandler(defaultErrorHandler);

        return factory;
    }

    @Bean
    public DefaultErrorHandler customDefaultErrorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);
        FixedBackOff backOff = new FixedBackOff(3000L, 3);

        return new DefaultErrorHandler(recoverer, backOff);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserCreatedEventDTO> userCreatedTopicListenerFactory(
            DefaultErrorHandler defaultErrorHandler) {
        return this.createListenerFactory(UserCreatedEventDTO.class, defaultErrorHandler);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NotificationCreatedEventDTO> notificationCreatedTopicListenerFactory(
            DefaultErrorHandler defaultErrorHandler) {
        return this.createListenerFactory(NotificationCreatedEventDTO.class, defaultErrorHandler);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NotificationPreferencesUpdatedEventDTO> notificationPreferencesUpdatedTopicListenerFactory(
            DefaultErrorHandler defaultErrorHandler) {
        return this.createListenerFactory(NotificationPreferencesUpdatedEventDTO.class, defaultErrorHandler);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AuthOtpRegisterByEmailEventDTO> authOtpRegisterByEmailTopicListenerFactory(
            DefaultErrorHandler defaultErrorHandler) {
        return this.createListenerFactory(AuthOtpRegisterByEmailEventDTO.class, defaultErrorHandler);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AuthOtpRegisterByPhoneEventDTO> authOtpRegisterByPhoneTopicListenerFactory(
            DefaultErrorHandler defaultErrorHandler) {
        return this.createListenerFactory(AuthOtpRegisterByPhoneEventDTO.class, defaultErrorHandler);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AuthOtpForgotPasswordByEmailEventDTO> authOtpForgotPasswordByEmailTopicListenerFactory(
            DefaultErrorHandler defaultErrorHandler) {
        return this.createListenerFactory(AuthOtpForgotPasswordByEmailEventDTO.class, defaultErrorHandler);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AuthOtpForgotPasswordByPhoneEventDTO> authOtpForgotPasswordByPhoneTopicListenerFactory(
            DefaultErrorHandler defaultErrorHandler) {
        return this.createListenerFactory(AuthOtpForgotPasswordByPhoneEventDTO.class, defaultErrorHandler);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AuthOtpSecurityEventDTO> authOtpSecurityTopicListenerFactory(
            DefaultErrorHandler defaultErrorHandler) {
        return this.createListenerFactory(AuthOtpSecurityEventDTO.class, defaultErrorHandler);
    }
}
