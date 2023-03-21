package com.project.challenge.infrastructure.rest.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.challenge.application.adapter.HistoryCommandService;
import com.project.challenge.domain.entity.History;
import com.project.challenge.infrastructure.ratelimit.RateLimiterService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestFilter.class);

    private static final String HEADER_LIMIT_REMAINING = "X-Rate-Limit-Remaining";
    private static final String HEADER_RETRY_AFTER = "X-Rate-Limit-Retry-After-Seconds";
    private static final String ERROR_MESSAGE_TOO_MANY_REQUESTS = "You have exhausted your API Request Quota";


    private ObjectMapper objectMapper;

    @Autowired
    private HistoryCommandService historyCommandService;

    @Autowired
    private RateLimiterService rateLimiterService;

    public RequestFilter(ObjectMapper objectMapper, HistoryCommandService historyCommandService, RateLimiterService rateLimiterService) {
        this.objectMapper = objectMapper;
        this.historyCommandService = historyCommandService;
        this.rateLimiterService = rateLimiterService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Bucket bucket = rateLimiterService.resolveBucket(request.getRemoteHost());

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.setHeader(HEADER_LIMIT_REMAINING, String.valueOf(probe.getRemainingTokens()));
        } else {
            long secondsToWaitForRefill = TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setHeader(HEADER_RETRY_AFTER, String.valueOf(secondsToWaitForRefill));
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), ERROR_MESSAGE_TOO_MANY_REQUESTS);
            return;
        }

        ContentCachingResponseWrapper resp = new ContentCachingResponseWrapper(response);

        filterChain.doFilter(request, resp);

        var history = getEntity(request, response, resp);

        resp.copyBodyToResponse();

        if (history != null) this.saveAsync(history);
    }

    private History getEntity(HttpServletRequest request, HttpServletResponse response, ContentCachingResponseWrapper resp) {
        try {
            byte[] responseBody = resp.getContentAsByteArray();
            String responseBodyString = new String(responseBody, StandardCharsets.UTF_8);
            JsonNode responseJson = objectMapper.readTree(responseBodyString);

            return History.builder()
                    .timestamp(new Timestamp(System.currentTimeMillis()))
                    .method(request.getMethod())
                    .uri(request.getRequestURI())
                    .statusCode(response.getStatus())
                    .response(responseJson)
                    .build();
        } catch (IOException e) {
            LOGGER.error("Failed to parse response body: {}", e.getMessage());
            return null;
        }
    }

    @Async
    void saveAsync(History history) {
        historyCommandService.save(history);
    }

}
