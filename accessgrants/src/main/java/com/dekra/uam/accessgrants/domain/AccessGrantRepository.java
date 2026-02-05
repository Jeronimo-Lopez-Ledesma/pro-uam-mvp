package com.dekra.uam.accessgrants.domain;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.Collection;

public interface AccessGrantRepository extends ReactiveMongoRepository<AccessGrant, String> {
    Flux<AccessGrant> findBySubjectOidAndScopeAndResourceIdIn(String subjectOid, String scope, Collection<String> resourceIds);
}
