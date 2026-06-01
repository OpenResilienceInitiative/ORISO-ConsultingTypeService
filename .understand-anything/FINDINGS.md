# ORISO-ConsultingTypeService Findings

## Navigation

- [High Priority](#high-priority)
- [Medium Priority](#medium-priority)
- [Documentation Gaps](#documentation-gaps)
- [Dead Code And Drift](#dead-code-and-drift)
- [Duplicated Logic](#duplicated-logic)
- [Architecture Boundary Risks](#architecture-boundary-risks)

## High Priority

1. Java build/runtime mismatch. [pom.xml](../pom.xml) sets Java 21 and Spring Boot 4.0.1, but [Dockerfile](../Dockerfile) uses eclipse-temurin:11-jre. A Java 21 class file will not run on a Java 11 runtime.

2. Consulting type JSON validation is disabled. [ConsultingTypeValidator.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/consultingtypes/ConsultingTypeValidator.java) contains a validation method that only comments VALIDATION DISABLED. Startup seed files can drift away from [src/main/resources/schemas/consulting-type.json](../src/main/resources/schemas/consulting-type.json) without being rejected.

3. Duplicate/conflicting JPA DDL config. [application.properties](../src/main/resources/application.properties) sets spring.jpa.hibernate.ddl-auto=validate and later sets spring.jpa.hibernate.ddl-auto=none. The later value wins, so the earlier validation intent is misleading.

4. Tenant-aware slug lookup appears wrong in technical tenant context. In [ConsultingTypeMongoTenantAwareRepositoryService.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/consultingtypes/ConsultingTypeMongoTenantAwareRepositoryService.java), findBySlug converts the technical-tenant query result but does not return it; execution continues to the tenantId-scoped query.

## Medium Priority

5. [Dockerfile](../Dockerfile) has unused ARG JAR_FILE and unused JAVA_UPPER_VERSION. It also starts JDWP on port 5005 by default.

6. Service port drift. Spring config defaults to server.port=8083; Docker exposes 8080.

7. Liquibase ownership is unclear. This repo has changelogs, while properties say database schemas are managed separately in ORISO-Database and Liquibase is disabled.

8. Checkstyle is advisory only. [pom.xml](../pom.xml) configures failsOnError=false and failOnViolation=false.

9. Several dependency versions are old or likely compatibility-sensitive for Spring Boot 4: Springdoc 2.0.0, OpenAPI Generator 6.2.1, Ehcache 2, Liquibase 4.9.1/plugin 4.1.1, commons-io 2.8.0, org.json 20201115, and Everit JSON Schema 1.12.2.

10. [ApplicationSettingsServiceFacade.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/service/ApplicationSettingsServiceFacade.java) silently does nothing when no settings document exists. Default initialization usually prevents this, but the failure mode is unclear.

## Documentation Gaps

- No current root architecture summary explains the mixed MongoDB/MariaDB design.
- No documented rule for ORISO-Database vs in-repo Liquibase changelog ownership.
- No explicit role/authority matrix for admin endpoints.
- No documented cache invalidation process for AgencyService/UserService when consulting type settings change.
- Existing [documentation](../documentation) contains a graph image/source but is not connected to onboarding docs.
- Both [README.md](../README.md) and [readme.md](../readme.md) are tracked, which is risky on case-insensitive filesystems.

## Dead Code And Drift

- [ConsultingTypeValidator.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/consultingtypes/ConsultingTypeValidator.java) has an unused buildSchema path because validation is disabled.
- [TopicController.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/controller/TopicController.java) autowires AuthenticatedUser but does not use it.
- Keycloak admin client dependency and local admin config are present, but active admin client usage was not found in production code.
- [Dockerfile](../Dockerfile) has unused ARG JAR_FILE and unused JAVA_UPPER_VERSION.
- springfox property names remain while the dependency is springdoc.

## Duplicated Logic

- Tenant-aware and tenant-unaware consulting type repository services duplicate lookup, uniqueness, save, update, and next-id behavior.
- Consulting type group repositories duplicate group-map assembly.
- OpenAPI Generator executions in [pom.xml](../pom.xml) repeat similar configuration blocks for each API spec.
- [application.properties](../src/main/resources/application.properties) and [application-local.properties](../src/main/resources/application-local.properties) duplicate Keycloak, datasource, MongoDB, server, and multitenancy settings.

## Architecture Boundary Risks

- Topic feature authorization crosses Topic, ApplicationSettings, TenantService, and TenantContext in one service.
- TenantHibernateInterceptor reads application-local.properties directly if an env var is absent.
- Consulting type startup loading writes directly to repositories at PostConstruct.
- Public/private endpoint rules are split between URL matchers, method authorization, and custom CSRF whitelist logic.
