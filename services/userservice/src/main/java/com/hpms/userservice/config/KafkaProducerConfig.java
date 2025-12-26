package com.hpms.userservice.config;

import com.hpms.commonlib.dto.DeleteUserRelatedEvent;
import com.hpms.commonlib.dto.EmailEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.converter.JacksonJsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

//@Configuration
public class KafkaProducerConfig {

    @Value("${kafka.topics.email-events:email-events-topic}")
    private String emailTopic;

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Bean
    public RecordMessageConverter converter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public NewTopic topic() {
        return new NewTopic(emailTopic, 1, (short) 1);
    }
}
