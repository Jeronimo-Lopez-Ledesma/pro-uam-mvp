package com.dekra.uam.accessgrants.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "memberships")
public record Membership(
        @Id String id,
        String subjectOid,
        String resourceId
) {
}
