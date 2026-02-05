package com.dekra.uam.accessgrants.application;

import reactor.core.publisher.Flux;

public interface SubscriptionQueryPort {
    Flux<String> listResourceIdsBySubscriber(String consumerId, String resourceType, String subscriberOid);
}
