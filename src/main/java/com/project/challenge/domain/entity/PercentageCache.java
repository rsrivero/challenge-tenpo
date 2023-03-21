package com.project.challenge.domain.entity;

import lombok.Data;
import java.time.Instant;

@Data
public class PercentageCache {

    private Long expirationAt;
    private double tax;

    public boolean isExpired() {
        return Instant.now().toEpochMilli() >= expirationAt;
    }
}