package com.dekra.uam.sample.opportunity.config;

import com.dekra.uam.enforcement.cache.CachedVisibilityClient;
import com.dekra.uam.enforcement.client.VisibilityClient;
import com.dekra.uam.enforcement.client.WebClientVisibilityClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Configuration
public class EnforcementConfig {

    @Bean
    VisibilityClient visibilityClient(WebClient.Builder builder,
                                      @Value("${accessgrants.base-url:http://localhost:8082}") String baseUrl) {
        return new WebClientVisibilityClient(builder.baseUrl(baseUrl).build());
    }

    @Bean
    CachedVisibilityClient cachedVisibilityClient(VisibilityClient visibilityClient,
                                                  @Value("${enforcement.cache.ttl-seconds:600}") long ttlSeconds) {
        return new CachedVisibilityClient(visibilityClient, Duration.ofSeconds(ttlSeconds));
    }
}
