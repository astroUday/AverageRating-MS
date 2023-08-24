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
public class UpdateListner {

    private final AverageRatingsService service;
    private int delayMs=1000*5;
    private Map<String,String> map=new HashMap<>();
    private static final HttpServletRequest request = RequestContextManager.getRequestContext();
    @KafkaListener(topics = "update-rating",groupId = "_update")
    public void updateThisRating(List<ConsumerRecord<Integer,String>> consumerRecordList, Acknowledgment acknowledgment) throws InterruptedException {

        AtomicReference<Integer> offset= new AtomicReference<>(0);
        Future<CompletableFuture<List<ConsumerRecord<Integer,String>>>> completableFuture=
                taskExecutor().submit(()-> {
                    CustomServiceResponse customServiceResponse=service.updateAverageRatings(consumerRecordList);
                    offset.set(customServiceResponse.getOffset());
                    return customServiceResponse.getCompletableFuture();
                });

        Thread.sleep(delayMs);
        try {
            CompletableFuture<List<ConsumerRecord<Integer,String>>> completableFutureFuture = completableFuture.get();

            if(completableFutureFuture!=null && completableFutureFuture.get()!=null){
                map.put("Authorization",request.getHeader("Authorization"));
                service.sendDataToRetryTopic(AverageRatingsService.MovieServiceBaseUrl,"kafka/retryUpdateTopic", ResponseEntity.class,map);
            }
        } catch (InterruptedException | ExecutionException e){
            e.printStackTrace();
        }

        acknowledgment.acknowledge();     // processed completely/partially
    }
}
