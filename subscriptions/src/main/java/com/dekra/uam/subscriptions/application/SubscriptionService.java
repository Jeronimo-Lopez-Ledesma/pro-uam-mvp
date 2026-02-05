package com.dekra.uam.subscriptions.application;

import com.dekra.uam.subscriptions.domain.SubscriptionEntry;
import com.dekra.uam.subscriptions.domain.SubscriptionRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Set;

@Service
public class SubscriptionService {

    private final SubscriptionRepository repository;

    public SubscriptionService(SubscriptionRepository repository) {
        this.repository = repository;
    }

    public Mono<SubscriptionEntry> upsertSubscribers(String consumerId, String resourceType, String resourceId, Set<String> subscribers) {
        return repository.findByConsumerIdAndResourceTypeAndResourceId(consumerId, resourceType, resourceId)
                .defaultIfEmpty(new SubscriptionEntry(null, consumerId, resourceType, resourceId, Set.of(), Instant.now()))
                .map(existing -> new SubscriptionEntry(existing.id(), consumerId, resourceType, resourceId, subscribers, Instant.now()))
                .flatMap(repository::save);
    }

    public Flux<String> listResourceIds(String consumerId, String resourceType) {
        return repository.findByConsumerIdAndResourceType(consumerId, resourceType).map(SubscriptionEntry::resourceId);
    }

    public Flux<String> listResourceIdsBySubscriber(String consumerId, String resourceType, String subscriberOid) {
        return repository.findByConsumerIdAndResourceTypeAndSubscribersContaining(consumerId, resourceType, subscriberOid)
                .map(SubscriptionEntry::resourceId);
    }
}
