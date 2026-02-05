package com.dekra.uam.accessgrants.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "access_grants")
public record AccessGrant(
        @Id String id,
        String subjectOid,
        String resourceId,
        String scope,
        PolicyEffect effect
) {
}
