package com.dekra.uam.subscriptions.api;

import com.dekra.uam.subscriptions.application.SubscriptionService;
import com.dekra.uam.subscriptions.domain.SubscriptionEntry;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@RestController
@RequestMapping("/admin/subscriptions")
public class SubscriptionController {

    private final SubscriptionService service;

    public SubscriptionController(SubscriptionService service) {
        this.service = service;
    }

    @PutMapping
    public Mono<SubscriptionEntry> upsert(@RequestBody UpsertRequest request) {
        return service.upsertSubscribers(request.consumerId(), request.resourceType(), request.resourceId(), request.subscribers());
    }

    @GetMapping("/resource-ids")
    public Flux<String> listResourceIds(@RequestParam String consumerId, @RequestParam String resourceType,
                                        @RequestParam(required = false) String subscriberOid) {
        if (subscriberOid == null || subscriberOid.isBlank()) {
            return service.listResourceIds(consumerId, resourceType);
        }
        return service.listResourceIdsBySubscriber(consumerId, resourceType, subscriberOid);
    }

    public record UpsertRequest(String consumerId, String resourceType, String resourceId, Set<String> subscribers) {}
}
