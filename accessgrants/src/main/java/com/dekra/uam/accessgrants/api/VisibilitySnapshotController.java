package com.dekra.uam.accessgrants.api;

import com.dekra.uam.accessgrants.application.VisibilitySnapshotService;
import com.dekra.uam.enforcement.model.VisibilityQueryPlan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Set;

@RestController
public class VisibilitySnapshotController {

    private final VisibilitySnapshotService service;

    public VisibilitySnapshotController(VisibilitySnapshotService service) {
        this.service = service;
    }

    @GetMapping("/internal/visibility-snapshots")
    public Mono<VisibilityQueryPlan> getSnapshot(@RequestParam String tenant,
                                                  @RequestParam String consumer,
                                                  @RequestParam String subjectOid,
                                                  @RequestParam String resourceType,
                                                  @RequestParam String scope,
                                                  @RequestHeader(name = "X-Roles", defaultValue = "") String rolesHeader) {
        Set<String> roles = rolesHeader.isBlank() ? Set.of() : Arrays.stream(rolesHeader.split(",")).map(String::trim).collect(java.util.stream.Collectors.toSet());
        return service.compute(tenant, consumer, subjectOid, resourceType, scope, roles);
    }
}
