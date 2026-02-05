package com.dekra.uam.sample.opportunity.api;

import com.dekra.uam.sample.opportunity.application.OpportunityQueryService;
import com.dekra.uam.sample.opportunity.domain.Opportunity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class OpportunityController {

    private final OpportunityQueryService queryService;

    public OpportunityController(OpportunityQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/opportunities")
    public Flux<Opportunity> getOpportunities(@RequestParam String consumer,
                                              @RequestParam String subject,
                                              @RequestParam(defaultValue = "tenant-a") String tenant) {
        return queryService.query(tenant, consumer, subject);
    }
}
