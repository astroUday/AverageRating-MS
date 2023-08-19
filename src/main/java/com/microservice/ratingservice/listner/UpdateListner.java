package com.microservice.ratingservice.listner;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@EnableKafka
public class UpdateListner {
    @KafkaListener(topics = "update-rating",groupId = "_updaate")
    public void updateThisRating(Object object){
        System.out.println(object);
    }
}
