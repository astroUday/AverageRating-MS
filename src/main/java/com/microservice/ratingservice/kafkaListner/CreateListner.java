package com.microservice.ratingservice.kafkaListner;

import com.microservice.ratingservice.model.CustomServiceResponse;
import com.microservice.ratingservice.service.AverageRatingsService;
import com.microservice.ratingservice.service.mvc.RequestContextManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import static com.microservice.ratingservice.RatingServiceApplication.taskExecutor;

@Component
@EnableKafka
@RequiredArgsConstructor
public class CreateListner {

    private final AverageRatingsService service;
    private int delayMs=1000*5;
    private Map<String,String> map=new HashMap<>();
    private static final HttpServletRequest request = RequestContextManager.getRequestContext();
    @KafkaListener(topics = "create-rating", groupId = "_create")       //reading max 1000 messages at a time
    public void createThisRating(List<ConsumerRecord<Integer,String>> consumerRecordList, Acknowledgment acknowledgment) throws InterruptedException {

        AtomicReference<Integer> offset= new AtomicReference<>(0);
        Future<CompletableFuture<List<ConsumerRecord<Integer,String>>>> completableFuture=
                taskExecutor().submit(()-> {
                    CustomServiceResponse customServiceResponse=service.calculateAverageRatings(consumerRecordList);
                    offset.set(customServiceResponse.getOffset());
                    return customServiceResponse.getCompletableFuture();
                });

        Thread.sleep(delayMs);
        try {
            CompletableFuture<List<ConsumerRecord<Integer,String>>> completableFutureFuture = completableFuture.get();

            if(completableFutureFuture!=null && completableFutureFuture.get()!=null){       // send messages to retry topic
                map.put("Authorization",request.getHeader("Authorization"));
                AverageRatingsService.sendDataToRetryTopic(AverageRatingsService.MovieServiceBaseUrl,"kafka/retryCreateTopic", ResponseEntity.class,map);
            }
        } catch (InterruptedException | ExecutionException e){
            e.printStackTrace();
        }

        acknowledgment.acknowledge();     // processed the data completely/partially
    }
}

