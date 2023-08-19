package com.microservice.ratingservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
@Configuration
public class ProducerConfig {
    @Bean
    public KafkaTemplate<String,String> kafkaTemplate(
            ProducerFactory<String, String> producerFactory ){
        return new KafkaTemplate<>(producerFactory);
    }
}
