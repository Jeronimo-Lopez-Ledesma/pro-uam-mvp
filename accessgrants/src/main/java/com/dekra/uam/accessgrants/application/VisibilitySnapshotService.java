package com.dekra.uam.accessgrants.application;

import com.dekra.uam.accessgrants.domain.*;
import com.dekra.uam.enforcement.model.VisibilityMode;
import com.dekra.uam.enforcement.model.VisibilityQueryPlan;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
public class VisibilitySnapshotService {

    private final SubscriptionQueryPort subscriptionQueryPort;
    private final PolicyRepository policyRepository;
    private final AccessGrantRepository accessGrantRepository;

    public VisibilitySnapshotService(SubscriptionQueryPort subscriptionQueryPort,
                                     PolicyRepository policyRepository,
                                     AccessGrantRepository accessGrantRepository) {
        this.subscriptionQueryPort = subscriptionQueryPort;
        this.policyRepository = policyRepository;
        this.accessGrantRepository = accessGrantRepository;
    }

    public Mono<VisibilityQueryPlan> compute(String tenant, String consumer, String subjectOid, String resourceType, String scope, Set<String> roles) {
        return subscriptionQueryPort.listResourceIdsBySubscriber(consumer, resourceType, subjectOid)
                .collectList()
                .flatMap(universe -> policyRepository.findByTenantIdAndResourceTypeAndScope(tenant, resourceType, scope)
                        .filter(policy -> policy.appliesToRoles() != null && policy.appliesToRoles().stream().anyMatch(roles::contains))
                        .collectList()
                        .flatMap(policies -> applyPolicyAndOverrides(subjectOid, scope, universe, policies)));
    }

    private Mono<VisibilityQueryPlan> applyPolicyAndOverrides(String subjectOid, String scope, List<String> universe, List<Policy> policies) {
        boolean hasDeny = policies.stream()
                .sorted(Comparator.comparingInt(Policy::priority))
                .anyMatch(policy -> policy.effect() == PolicyEffect.DENY);

        if (hasDeny) {
            return Mono.just(new VisibilityQueryPlan(VisibilityMode.IDS, List.of(), null, 600));
        }

        return accessGrantRepository.findBySubjectOidAndScopeAndResourceIdIn(subjectOid, scope, universe)
                .collectList()
                .map(overrides -> {
                    List<String> filtered = universe.stream().filter(id -> {
                        AccessGrant matching = overrides.stream().filter(grant -> grant.resourceId().equals(id)).findFirst().orElse(null);
                        if (matching == null) {
                            return true;
                        }
                        return matching.effect() == PolicyEffect.ALLOW;
                    }).toList();
                    return new VisibilityQueryPlan(VisibilityMode.IDS, filtered, null, 600);
                });
    }
}
