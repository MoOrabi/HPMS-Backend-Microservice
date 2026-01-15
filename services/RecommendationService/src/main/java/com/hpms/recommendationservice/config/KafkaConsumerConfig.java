package com.hpms.recommendationservice.config;

import com.hpms.recommendationservice.dto.JobPostEvent;
import com.hpms.recommendationservice.dto.JobSeekerEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.DelegatingByTopicDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Map<Pattern, Deserializer<?>> topicDeserializers = new HashMap<>();

        JsonDeserializer<JobSeekerEvent> jobSeekerDeserializer =
                new JsonDeserializer<>(JobSeekerEvent.class, false);
        jobSeekerDeserializer.addTrustedPackages("*");

        JsonDeserializer<JobPostEvent> jobPostDeserializer =
                new JsonDeserializer<>(JobPostEvent.class, false);
        jobPostDeserializer.addTrustedPackages("*");

        topicDeserializers.put(
                Pattern.compile("^jobseeker-events$"),
                jobSeekerDeserializer
        );

        topicDeserializers.put(
                Pattern.compile("^job-events$"),
                jobPostDeserializer
        );


        DelegatingByTopicDeserializer valueDeserializer =
                new DelegatingByTopicDeserializer(
                        topicDeserializers,
                        new ByteArrayDeserializer() // fallback
                );

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object>
    kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
