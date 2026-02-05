package com.dekra.uam.enforcement.model;

import java.util.List;

public record ResourcePredicateValue(
        PredicateOperator operator,
        String field,
        String value,
        List<String> values,
        List<ResourcePredicateValue> children
) {
}
