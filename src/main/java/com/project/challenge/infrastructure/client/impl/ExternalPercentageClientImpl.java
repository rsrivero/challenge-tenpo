package com.project.challenge.infrastructure.client.impl;

import com.project.challenge.application.adapter.PercentageService;
import com.project.challenge.infrastructure.client.dto.PercentageResponseDTO;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class ExternalPercentageClientImpl implements PercentageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalPercentageClientImpl.class);

    public static final String PERCENTAGE_CACHE = "PERCENTAGE_CACHE";

    private final WebClient webClient = WebClient.create("https://mocki.io");

    @Autowired
    private Map<String, Double> percentageMap;

    @Override
    public Double retrieve() {
        var percentageCache = this.percentageMap.get(PERCENTAGE_CACHE);

        if (percentageCache != null) {
            return percentageCache;
        }else{
            return this.retrieveFromServer();
        }
    }

    public Double retrieveFromServer() {
        var response = retrievePercentage();
        if (response.blockOptional().isPresent()){
            var entity = response.block();
            return entity.getPercentage();
        }
        return null;
    }

    @Retry(name = "externalRetry",fallbackMethod = "findLastInCache")
    public Mono<PercentageResponseDTO> retrievePercentage() {
        LOGGER.info("YENDO AL SERVIDOR");
        return webClient.get()
                    .uri("/v1/223e5883-e12e-428e-bfe9-e5df61a85fc7")
                    .retrieve()
                    .bodyToMono(PercentageResponseDTO.class)
                    .doOnNext(response -> this.percentageMap.put(PERCENTAGE_CACHE, response.getPercentage()));
    }

    private Mono<PercentageResponseDTO> findLastInCache(Exception ex) {
        var percentageCache = this.percentageMap.get(PERCENTAGE_CACHE);

        if (percentageCache != null) {
            return Mono.just(PercentageResponseDTO.builder().percentage(percentageCache).build());
        }else{
            return null;
        }
    }
}
