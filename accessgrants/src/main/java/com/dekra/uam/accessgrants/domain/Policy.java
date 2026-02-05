package com.dekra.uam.accessgrants.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document(collection = "policies")
public record Policy(
        @Id String id,
        int priority,
        PolicyEffect effect,
        String tenantId,
        String resourceType,
        String scope,
        Set<String> appliesToRoles
) {
}
