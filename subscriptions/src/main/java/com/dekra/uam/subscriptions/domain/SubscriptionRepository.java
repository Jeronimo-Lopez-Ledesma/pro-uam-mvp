package com.dekra.uam.subscriptions.domain;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SubscriptionRepository extends ReactiveMongoRepository<SubscriptionEntry, String> {
    Mono<SubscriptionEntry> findByConsumerIdAndResourceTypeAndResourceId(String consumerId, String resourceType, String resourceId);
    Flux<SubscriptionEntry> findByConsumerIdAndResourceType(String consumerId, String resourceType);
    Flux<SubscriptionEntry> findByConsumerIdAndResourceTypeAndSubscribersContaining(String consumerId, String resourceType, String subscriber);
}
