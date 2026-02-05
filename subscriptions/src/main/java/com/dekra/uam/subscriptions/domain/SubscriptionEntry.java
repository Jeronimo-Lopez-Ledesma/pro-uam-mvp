package com.dekra.uam.subscriptions.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Set;

@Document(collection = "subscription_entries")
@CompoundIndexes({
        @CompoundIndex(name = "uniq_consumer_resource", def = "{'consumerId':1,'resourceType':1,'resourceId':1}", unique = true),
        @CompoundIndex(name = "idx_consumer_resourceType", def = "{'consumerId':1,'resourceType':1}"),
        @CompoundIndex(name = "idx_consumer_resource_subscribers", def = "{'consumerId':1,'resourceType':1,'subscribers':1}")
})
public record SubscriptionEntry(
        @Id String id,
        String consumerId,
        String resourceType,
        String resourceId,
        Set<String> subscribers,
        Instant updatedAt
) {
}
