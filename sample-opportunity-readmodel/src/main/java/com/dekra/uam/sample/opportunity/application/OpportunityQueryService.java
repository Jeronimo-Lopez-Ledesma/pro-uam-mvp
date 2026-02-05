package com.dekra.uam.sample.opportunity.application;

import com.dekra.uam.enforcement.cache.CachedVisibilityClient;
import com.dekra.uam.enforcement.model.PredicateOperator;
import com.dekra.uam.enforcement.model.ResourcePredicateValue;
import com.dekra.uam.enforcement.model.VisibilityMode;
import com.dekra.uam.sample.opportunity.domain.Opportunity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Set;

@Service
public class OpportunityQueryService {

    private final OpportunityRepository repository;
    private final CachedVisibilityClient cachedVisibilityClient;

    public OpportunityQueryService(OpportunityRepository repository, CachedVisibilityClient cachedVisibilityClient) {
        this.repository = repository;
        this.cachedVisibilityClient = cachedVisibilityClient;
    }

    public Flux<Opportunity> query(String tenant, String consumer, String subject) {
        return cachedVisibilityClient.getPlan(tenant, consumer, subject, "opportunity", "read")
                .flatMapMany(plan -> repository.findAll().filter(opportunity -> matches(plan.mode(), plan.ids(), plan.predicate(), opportunity)));
    }

    private boolean matches(VisibilityMode mode, java.util.List<String> ids, ResourcePredicateValue predicate, Opportunity opportunity) {
        if (mode == VisibilityMode.IDS) {
            return Set.copyOf(ids).contains(opportunity.id());
        }
        return evaluate(predicate, opportunity);
    }

    private boolean evaluate(ResourcePredicateValue predicate, Opportunity opportunity) {
        if (predicate == null) {
            return true;
        }
        return switch (predicate.operator()) {
            case EQ -> resolveField(predicate.field(), opportunity).equals(predicate.value());
            case IN -> predicate.values().contains(resolveField(predicate.field(), opportunity));
            case AND -> predicate.children().stream().allMatch(child -> evaluate(child, opportunity));
            case OR -> predicate.children().stream().anyMatch(child -> evaluate(child, opportunity));
            case NOT -> !evaluate(predicate.children().getFirst(), opportunity);
        };
    }

    private String resolveField(String field, Opportunity o) {
        return switch (field) {
            case "id" -> o.id();
            case "ownerOid" -> o.ownerOid();
            case "status" -> o.status();
            default -> "";
        };
    }
}
