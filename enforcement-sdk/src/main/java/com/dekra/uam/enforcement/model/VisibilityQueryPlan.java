package com.dekra.uam.enforcement.model;

import java.util.List;

public record VisibilityQueryPlan(
        VisibilityMode mode,
        List<String> ids,
        ResourcePredicateValue predicate,
        long ttlSeconds
) {
}
