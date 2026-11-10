package vn.tuhoc.UserService.configs;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic authOtpRegisterByEmailTopic() {
        return TopicBuilder
                .name("auth_otp_register_by_email")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic authOtpRegisterByPhoneTopic() {
        return TopicBuilder
                .name("auth_otp_register_by_phone")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic authOtpForgotPasswordByEmailTopic() {
        return TopicBuilder
                .name("auth_otp_forgot_password_by_email")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic authOtpSecurityTopic() {
        return TopicBuilder.name("auth_otp_security").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic userCreatedTopic() {
        return TopicBuilder
                .name("user_created")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic notificationPreferencesUpdatedTopic() {
        return TopicBuilder
                .name("notification_preferences_updated")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
