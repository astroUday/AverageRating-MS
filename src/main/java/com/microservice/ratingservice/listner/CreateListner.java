package com.microservice.ratingservice.listner;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@EnableKafka
public class CreateListner {

    @KafkaListener(topics = "create-rating", groupId = "_create")
    public void createThisRating(Object object){
        System.out.println(object);
    }
}
