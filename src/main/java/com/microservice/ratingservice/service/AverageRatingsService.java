package com.microservice.ratingservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microservice.ratingservice.dto.ConsumerDto;
import com.microservice.ratingservice.dto.MovieRatingsResponseDto;
import com.microservice.ratingservice.model.AverageRatings;
import com.microservice.ratingservice.model.CustomServiceResponse;
import com.microservice.ratingservice.repo.AveraegeRatingsRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import static com.microservice.ratingservice.config.ObjectMapperConfig.objectMapper;
import static com.microservice.ratingservice.config.WebclientConfig.webclient;
@Service
@RequiredArgsConstructor
public class AverageRatingsService {


    private final AveraegeRatingsRepo averageRatingsRepo;
    @PersistenceContext
    private final EntityManager entityManager;
    public static final String MovieServiceBaseUrl="http://MOVIE-SERVICE/api/film/";
    private List<ConsumerRecord<Integer,String>> unProcessedConsumerRecords=new ArrayList<>();



    public MovieRatingsResponseDto findFinalMovieRating(String movieId){
        return averageRatingsRepo.findByMovieId(movieId,true);
    }
    @Transactional
    public boolean addMovie(String movieId) {
        if(!averageRatingsRepo.existsByMovieId(movieId)){
            AverageRatings averageRatings=new AverageRatings();
            averageRatings.setMovieId(movieId);
            averageRatingsRepo.save(averageRatings);
            return true;
        }
        return false;
    }
    @Transactional
    public CustomServiceResponse<List<ConsumerRecord<Integer,String>>> calculateAverageRatings(List<ConsumerRecord<Integer, String>> consumerRecordList){

        CompletableFuture<List<ConsumerRecord<Integer,String>>> completableFuture=new CompletableFuture<>();
        AtomicReference<Integer> offset= new AtomicReference<>(0);

        consumerRecordList.forEach( consumerRecord ->{
            try {
                ConsumerDto consumerDto=objectMapper().readValue(consumerRecord.value(),ConsumerDto.class);
                AverageRatings temp=averageRatingsRepo.findByMovieId(consumerDto.getMovieId());

                if(temp!=null){
                    var sum = (temp.getAverageRating() * temp.getNumberOfRatings()) + consumerDto.getRating();
                    Double newRating = sum/(temp.getNumberOfRatings()+1);
                    temp.setAverageRating(newRating);
                    temp.setNumberOfRatings(temp.getNumberOfRatings()+1);

                    try {
                        entityManager.persist(temp);
                        completableFuture.complete(null);
                        offset.getAndSet(offset.get() + 1);
                    }
                    catch (OptimisticLockException exception){
                        exception.printStackTrace();
                        unProcessedConsumerRecords.add(consumerRecord);
                        completableFuture.complete(unProcessedConsumerRecords);
                    }
                }
                else {
                    AverageRatings averageRatings=new AverageRatings();
                    averageRatings.setMovieId(temp.getMovieId());
                    averageRatings.setNumberOfRatings(1l);
                    averageRatings.setAverageRating((double)consumerDto.getRating());

                    try {
                        entityManager.persist(averageRatings);
                        completableFuture.complete(null);
                        offset.getAndSet(offset.get() + 1);
                    }
                    catch (OptimisticLockException exception){
                        exception.printStackTrace();
                        unProcessedConsumerRecords.add(consumerRecord);
                        completableFuture.complete(unProcessedConsumerRecords);
                    }
                }

            } catch (JsonProcessingException e) {
                unProcessedConsumerRecords.add(consumerRecord);
                completableFuture.complete(unProcessedConsumerRecords);
                e.printStackTrace();
            }

        });
        return new CustomServiceResponse(completableFuture,offset.get());
    }
    @Transactional
    public CustomServiceResponse<List<ConsumerRecord<Integer,String>>> updateAverageRatings(List<ConsumerRecord<Integer, String>> consumerRecordList) {

        CompletableFuture<List<ConsumerRecord<Integer,String>>> completableFuture=new CompletableFuture<>();
        AtomicReference<Integer> offset= new AtomicReference<>(0);

        consumerRecordList.forEach(consumerRecord ->{
            try{
                ConsumerDto consumerDto=objectMapper().readValue(consumerRecord.value(),ConsumerDto.class);
                AverageRatings temp=averageRatingsRepo.findByMovieId(consumerDto.getMovieId());
                var sum= (temp.getAverageRating()*temp.getNumberOfRatings()) + consumerDto.getDifference();
                Double newRating= sum/ temp.getNumberOfRatings();
                temp.setAverageRating(newRating);

                try {
                    offset.getAndSet(offset.get() + 1);
                    entityManager.persist(temp);
                    completableFuture.complete(null);
                }
                catch (OptimisticLockException exception){
                    exception.printStackTrace();
                    unProcessedConsumerRecords.add(consumerRecord);
                    completableFuture.complete(unProcessedConsumerRecords);
                }
            }
            catch (JsonProcessingException e){
                unProcessedConsumerRecords.add(consumerRecord);
                completableFuture.complete(unProcessedConsumerRecords);
                e.printStackTrace();
            }
        });
        return new CustomServiceResponse(completableFuture,offset.get());
    }

    public static <T> Mono<T> sendDataToRetryTopic(String baseUrl, String uri, Class<T> responseType, Map<String,String> headers) {
        return webclient()
                .baseUrl(baseUrl)
                .build()
                .post()
                .uri(uri)
                .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                .retrieve()
                .bodyToMono(responseType);
    }


}
