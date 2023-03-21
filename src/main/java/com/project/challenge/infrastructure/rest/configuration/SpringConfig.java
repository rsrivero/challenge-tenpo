package com.project.challenge.infrastructure.rest.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hazelcast.config.Config;
import com.hazelcast.config.ManagementCenterConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.project.challenge.application.adapter.HistoryCommandService;
import com.project.challenge.infrastructure.client.impl.ExternalPercentageClientImpl;
import com.project.challenge.infrastructure.ratelimit.RateLimiterService;
import com.project.challenge.infrastructure.rest.filter.RequestFilter;
import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TimeZone;

import static com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE;

@Configuration
@EnableAutoConfiguration
public class SpringConfig implements WebMvcConfigurer {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    @Bean
    @Autowired
    public FilterRegistrationBean<RequestFilter> loggingFilter(ObjectMapper objectMapper, HistoryCommandService historyCommandService, RateLimiterService rateLimiterService) {
        FilterRegistrationBean<RequestFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestFilter(objectMapper(), historyCommandService, rateLimiterService));
        return registrationBean;
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return defaultObjectMapper();
    }

    public static ObjectMapper defaultObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);
        objectMapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setPropertyNamingStrategy(SNAKE_CASE);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        objectMapper.setDateFormat(sdf);
        return objectMapper;
    }

    @Bean
    public Config hazelCastConfig() {
        return new Config().setManagementCenterConfig(
                new ManagementCenterConfig()).addMapConfig(new MapConfig(ExternalPercentageClientImpl.PERCENTAGE_CACHE).setTimeToLiveSeconds(1800));
    }
    @Bean
    public HazelcastInstance hazelcastInstance(Config hazelCastConfig) {
        return Hazelcast.newHazelcastInstance(hazelCastConfig);
    }
    @Bean
    public Map<String, Double> percentageMap(HazelcastInstance hazelcastInstance) {
        return hazelcastInstance.getMap(ExternalPercentageClientImpl.PERCENTAGE_CACHE);
    }
    @Bean
    public Map<String, Bucket> rateLimitMap(HazelcastInstance hazelcastInstance) {
        return hazelcastInstance.getMap(ExternalPercentageClientImpl.PERCENTAGE_CACHE);
    }

}