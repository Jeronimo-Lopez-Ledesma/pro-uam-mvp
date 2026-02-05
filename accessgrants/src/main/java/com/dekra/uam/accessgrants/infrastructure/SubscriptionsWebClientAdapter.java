package com.dekra.uam.accessgrants.infrastructure;

import com.dekra.uam.accessgrants.application.SubscriptionQueryPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class SubscriptionsWebClientAdapter implements SubscriptionQueryPort {

    private final WebClient webClient;

    public SubscriptionsWebClientAdapter(WebClient.Builder builder,
                                         @Value("${subscriptions.base-url:http://localhost:8081}") String subscriptionsBaseUrl) {
        this.webClient = builder.baseUrl(subscriptionsBaseUrl).build();
    }

    @Override
    public Flux<String> listResourceIdsBySubscriber(String consumerId, String resourceType, String subscriberOid) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/admin/subscriptions/resource-ids")
                        .queryParam("consumerId", consumerId)
                        .queryParam("resourceType", resourceType)
                        .queryParam("subscriberOid", subscriberOid)
                        .build())
                .retrieve()
                .bodyToFlux(String.class);
    }
}
