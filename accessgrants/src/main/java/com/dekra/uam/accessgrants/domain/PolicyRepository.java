package com.dekra.uam.accessgrants.domain;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface PolicyRepository extends ReactiveMongoRepository<Policy, String> {
    Flux<Policy> findByTenantIdAndResourceTypeAndScope(String tenantId, String resourceType, String scope);
}
