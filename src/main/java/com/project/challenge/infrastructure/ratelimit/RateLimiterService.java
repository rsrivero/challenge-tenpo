package com.project.challenge.infrastructure.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    private final Map<String, Bucket> localCache = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String apiKey) {
        return localCache.computeIfAbsent(apiKey, this::newBucket);
    }

    private Bucket newBucket(String apiKey) {
        Refill refill = Refill.intervally(3, Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(3, refill);
        return Bucket.builder().addLimit(limit).build();
    }

}
