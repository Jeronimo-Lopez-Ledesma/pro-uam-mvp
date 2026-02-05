package com.dekra.uam.enforcement.cache;

import com.dekra.uam.enforcement.client.VisibilityClient;
import com.dekra.uam.enforcement.model.VisibilityQueryPlan;
import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class CachedVisibilityClient {

    private final VisibilityClient delegate;
    private final AsyncCache<VisibilityKey, VisibilityQueryPlan> cache;

    public CachedVisibilityClient(VisibilityClient delegate, Duration ttl) {
        this.delegate = delegate;
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(ttl)
                .buildAsync();
    }

    public Mono<VisibilityQueryPlan> getPlan(String tenant, String consumer, String subjectOid, String resourceType, String scope) {
        VisibilityKey key = new VisibilityKey(tenant, consumer, subjectOid, resourceType, scope);
        CompletableFuture<VisibilityQueryPlan> future = cache.get(key, (k, executor) ->
                delegate.getPlan(k.tenant(), k.consumer(), k.subjectOid(), k.resourceType(), k.scope()).toFuture());
        return Mono.fromFuture(future);
    }

    public record VisibilityKey(String tenant, String consumer, String subjectOid, String resourceType, String scope) {
    }
}
