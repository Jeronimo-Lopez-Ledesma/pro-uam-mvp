package com.dekra.uam.enforcement.client;

import com.dekra.uam.enforcement.model.VisibilityQueryPlan;
import reactor.core.publisher.Mono;

public interface VisibilityClient {
    Mono<VisibilityQueryPlan> getPlan(String tenant, String consumer, String subjectOid, String resourceType, String scope);
}
