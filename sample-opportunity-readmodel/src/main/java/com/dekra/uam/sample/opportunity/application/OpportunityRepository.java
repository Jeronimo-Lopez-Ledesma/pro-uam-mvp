package com.dekra.uam.sample.opportunity.application;

import com.dekra.uam.sample.opportunity.domain.Opportunity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
public class OpportunityRepository {
    private static final List<Opportunity> DATA = List.of(
            new Opportunity("opp-1", "Fleet Deal", "subject-a", "OPEN"),
            new Opportunity("opp-2", "Renewal", "subject-b", "WON"),
            new Opportunity("opp-3", "Upsell", "subject-a", "OPEN")
    );

    public Flux<Opportunity> findAll() {
        return Flux.fromIterable(DATA);
    }
}
