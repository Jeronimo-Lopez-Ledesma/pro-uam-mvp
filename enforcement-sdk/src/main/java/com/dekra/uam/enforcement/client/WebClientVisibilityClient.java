package com.dekra.uam.enforcement.client;

import com.dekra.uam.enforcement.model.VisibilityQueryPlan;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class WebClientVisibilityClient implements VisibilityClient {

    private final WebClient webClient;

    public WebClientVisibilityClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<VisibilityQueryPlan> getPlan(String tenant, String consumer, String subjectOid, String resourceType, String scope) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/internal/visibility-snapshots")
                        .queryParam("tenant", tenant)
                        .queryParam("consumer", consumer)
                        .queryParam("subjectOid", subjectOid)
                        .queryParam("resourceType", resourceType)
                        .queryParam("scope", scope)
                        .build())
                .retrieve()
                .bodyToMono(VisibilityQueryPlan.class);
    }
}
