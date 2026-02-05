# uam-mvp

Multi-module Java 21 / Spring Boot 3.x reactive MVP for SPARTA-like authorization.

## Modules

- `uam-mvp-bom`: dependency management BOM.
- `subscriptions`: maintains consumer-scoped subscription universe in Mongo (`subscription_entries`).
- `accessgrants`: PDP service computing `VisibilityQueryPlan` combining subscriptions + policies + grants.
- `enforcement-sdk`: reusable WebClient + Caffeine cache for local enforcement.
- `sample-opportunity-readmodel`: demo read model applying plan locally.

## Local run

### Prerequisites

- Java 21
- Maven 3.9+
- MongoDB running locally (or override URI)

### Environment variables

- `MONGODB_URI` (used by `subscriptions` and `accessgrants`)
- `subscriptions.base-url` for `accessgrants` (default `http://localhost:8081`)
- `accessgrants.base-url` for sample read model (default `http://localhost:8082`)
- `enforcement.cache.ttl-seconds` for sample read model SDK cache (default `600`)

### Start services

```bash
mvn -q -DskipTests package
mvn -pl subscriptions spring-boot:run
mvn -pl accessgrants spring-boot:run
mvn -pl sample-opportunity-readmodel spring-boot:run
```

Ports:
- subscriptions: `8081`
- accessgrants: `8082`
- sample read model: `8083`

## Sample requests

```bash
# create subscription entries
curl -X PUT http://localhost:8081/admin/subscriptions \
  -H 'Content-Type: application/json' \
  -d '{"consumerId":"consumer-a","resourceType":"opportunity","resourceId":"opp-1","subscribers":["subject-a"]}'

# ask PDP directly
curl 'http://localhost:8082/internal/visibility-snapshots?tenant=tenant-a&consumer=consumer-a&subjectOid=subject-a&resourceType=opportunity&scope=read' \
  -H 'X-Roles: SALES'

# query readmodel (will use enforcement-sdk cache)
curl 'http://localhost:8083/opportunities?consumer=consumer-a&subject=subject-a'
```

## Test

```bash
mvn test
```
