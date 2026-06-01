# ORISO-ConsultingTypeService Onboarding

## Navigation

- [First Mental Model](#first-mental-model)
- [What To Read First](#what-to-read-first)
- [Local Development Checklist](#local-development-checklist)
- [Common Change Paths](#common-change-paths)
- [Testing Map](#testing-map)
- [Operational Notes](#operational-notes)
- [Review Checklist](#review-checklist)

## First Mental Model

This service is the catalog and settings authority for consulting types. It also owns topics, topic groups, and global application settings. Treat it as a contract-heavy backend: OpenAPI specs define the HTTP surface, Maven generates interfaces and DTOs, and handwritten classes implement behavior.

The quickest split to remember: consulting types and app settings are MongoDB; topics and topic groups are MariaDB/JPA; tenant lookups go to external TenantService; role grants come from Keycloak role claims mapped to local authorization values.

## What To Read First

1. [api/consultingtypeservice.yml](../api/consultingtypeservice.yml) - main catalog API.
2. [api/topicservice.yml](../api/topicservice.yml) - topic, topic admin, and topic groups API.
3. [SecurityConfig.java](../src/main/java/de/caritas/cob/consultingtypeservice/config/SecurityConfig.java) - what is public, authenticated, and method-protected.
4. [ConsultingTypeController.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/controller/ConsultingTypeController.java) and [ConsultingTypeService.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/service/ConsultingTypeService.java) - main request path.
5. [ConsultingTypeLoader.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/consultingtypes/ConsultingTypeLoader.java) - startup data loading.
6. [TopicServiceFacade.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/service/TopicServiceFacade.java) and [TopicService.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/service/TopicService.java) - topic behavior.
7. [ApplicationSettingsService.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/service/ApplicationSettingsService.java) and [DefaultApplicationSettingsInitializer.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/service/DefaultApplicationSettingsInitializer.java) - settings behavior.
8. [application.properties](../src/main/resources/application.properties) and [application-local.properties](../src/main/resources/application-local.properties) - runtime assumptions.
9. [pom.xml](../pom.xml) - generation, dependency versions, test phases, and style tooling.
10. [FINDINGS.md](FINDINGS.md) - current maintainability risks.

## Local Development Checklist

- Use Java 21 for builds because [pom.xml](../pom.xml) targets Java 21.
- Expect generated code under target/generated-sources; do not edit generated classes directly.
- Provide MongoDB and MariaDB URLs through SPRING_DATA_MONGODB_URI and SPRING_DATASOURCE_URL, or use a local environment that supplies them.
- If testing JWT-protected endpoints, configure the JWT issuer and Keycloak settings.
- If enabling multitenancy, configure TENANT_SERVICE_API_URL so TenantService client calls can resolve tenant data.
- Remember that [Dockerfile](../Dockerfile) currently uses Java 11 runtime, which does not match the Java 21 build target.

## Common Change Paths

### Add Or Change A Consulting Type Endpoint

1. Update [api/consultingtypeservice.yml](../api/consultingtypeservice.yml).
2. Rebuild so generated interfaces and DTOs refresh.
3. Implement or adjust [ConsultingTypeController.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/controller/ConsultingTypeController.java).
4. Put business behavior in [ConsultingTypeService.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/service/ConsultingTypeService.java), not the controller.
5. Add mapper and controller tests.

### Change Consulting Type Settings Shape

1. Update [src/main/resources/schemas/consulting-type.json](../src/main/resources/schemas/consulting-type.json).
2. Update seed JSON in [consulting-type-settings](../consulting-type-settings).
3. Rebuild generated schema classes.
4. Update converters and mappers if DTO shape changes.
5. Re-enable or update validation in [ConsultingTypeValidator.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/consultingtypes/ConsultingTypeValidator.java) if the schema should be enforced.
6. Coordinate downstream caches in AgencyService and UserService, as legacy docs say those services cache consulting type settings.

### Add Or Change Topic Data

1. Update [api/topicservice.yml](../api/topicservice.yml) if the HTTP contract changes.
2. Update [TopicEntity.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/model/TopicEntity.java) or [TopicGroupEntity.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/model/TopicGroupEntity.java).
3. Add Liquibase SQL under [src/main/resources/db/changelog/changeset](../src/main/resources/db/changelog/changeset).
4. Check whether the same schema change belongs in ORISO-Database.
5. Update converters, validation, and tests.

## Testing Map

- Controller integration tests: [src/test/java/de/caritas/cob/consultingtypeservice/api/controller](../src/test/java/de/caritas/cob/consultingtypeservice/api/controller).
- Consulting type repository and service tests: [src/test/java/de/caritas/cob/consultingtypeservice/api/consultingtypes](../src/test/java/de/caritas/cob/consultingtypeservice/api/consultingtypes).
- Topic service and repository tests: [src/test/java/de/caritas/cob/consultingtypeservice/api/service](../src/test/java/de/caritas/cob/consultingtypeservice/api/service) and [src/test/java/de/caritas/cob/consultingtypeservice/api/repository](../src/test/java/de/caritas/cob/consultingtypeservice/api/repository).
- Mapper and converter tests: [src/test/java/de/caritas/cob/consultingtypeservice/api/mapper](../src/test/java/de/caritas/cob/consultingtypeservice/api/mapper) and [src/test/java/de/caritas/cob/consultingtypeservice/api/converter](../src/test/java/de/caritas/cob/consultingtypeservice/api/converter).
- Security filter tests: [src/test/java/de/caritas/cob/consultingtypeservice/filter](../src/test/java/de/caritas/cob/consultingtypeservice/filter).

## Operational Notes

- Service port defaults to 8083 in Spring properties. Docker exposes 8080, so deployment manifests need to be explicit.
- Logs write to log/consultingtypeservice.log.
- Health endpoints are whitelisted under /actuator/health.
- Docs are configured at /consultingtypes/docs and /v3/api-docs.
- Tracing is present but disabled by default.

## Review Checklist

- OpenAPI spec and controller implementation still match.
- Generated code was not edited directly.
- Tenant-aware and tenant-unaware behavior both make sense.
- Mutation endpoints have clear authorization coverage.
- Mongo vs MariaDB ownership is clear for the changed data.
- Changelogs and ORISO-Database expectations are aligned.
- Docker runtime Java version matches build target.
