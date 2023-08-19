package com.microservice.ratingservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@EnableKafka
public class ProducerService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void produceMessage(String message) {
        kafkaTemplate.send("chicmics", message);
    }
}
