# ORISO-ConsultingTypeService Architecture

## Navigation

- [Overview](#overview)
- [Repository Map](#repository-map)
- [Architecture Layers](#architecture-layers)
- [Main Data Flows](#main-data-flows)
- [Authentication And Tenant Flow](#authentication-and-tenant-flow)
- [API Structure](#api-structure)
- [Persistence Model](#persistence-model)
- [Deployment And Configuration](#deployment-and-configuration)
- [Findings](#findings)
- [Generated Artifacts](#generated-artifacts)

## Overview

This repository is an OpenAPI-first Spring Boot service. Its stable public contracts are YAML specs under [api/](../api). Handwritten controllers implement generated interfaces, while services and repositories own behavior. The service has three domain responsibilities: consulting type catalog, topic taxonomy, and application settings.

The important design choice is mixed persistence. Consulting type settings are schema-shaped MongoDB documents. Application settings are a single MongoDB document. Topics and topic groups are relational entities in MariaDB. Multitenancy cuts through both sides: Mongo repository implementations switch between tenant-aware and tenant-unaware behavior, while JPA topics use explicit tenant queries plus Hibernate filter/interceptor support.

## Repository Map

- Build and code generation: [pom.xml](../pom.xml).
- API contracts: [api/consultingtypeservice.yml](../api/consultingtypeservice.yml), [api/topicservice.yml](../api/topicservice.yml), [api/applicationsettingsservice.yml](../api/applicationsettingsservice.yml), [api/consultingtypeadminservice.yml](../api/consultingtypeadminservice.yml).
- External client contract: [services/tenantservice.yaml](../services/tenantservice.yaml).
- Boot entrypoint: [ConsultingTypeServiceApplication.java](../src/main/java/de/caritas/cob/consultingtypeservice/ConsultingTypeServiceApplication.java).
- Runtime configuration: [application.properties](../src/main/resources/application.properties), [application-local.properties](../src/main/resources/application-local.properties).
- Container runtime: [Dockerfile](../Dockerfile).
- Existing static architecture image: [documentation/ConsultingTypeService-architecture.graphml](../documentation/ConsultingTypeService-architecture.graphml) and PNG.

## Architecture Layers

### API Contract Layer

The api YAML files are input to OpenAPI Generator in [pom.xml](../pom.xml). The generated Spring interfaces are implemented by controllers in api/controller and api/admin/controller. The same Maven build uses [services/tenantservice.yaml](../services/tenantservice.yaml) to generate TenantControllerApi client classes.

### HTTP Layer

Controllers are intentionally thin. [ConsultingTypeController.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/controller/ConsultingTypeController.java) serves catalog reads and protected create/patch operations. [TopicAdminController.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/controller/TopicAdminController.java) serves protected topic mutations. [ApplicationSettingsController.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/controller/ApplicationSettingsController.java) serves public settings reads and protected settings patches.

### Security And Tenancy Layer

[SecurityConfig.java](../src/main/java/de/caritas/cob/consultingtypeservice/config/SecurityConfig.java) builds a stateless filter chain. It disables Spring CSRF, adds [StatelessCsrfFilter.java](../src/main/java/de/caritas/cob/consultingtypeservice/filter/StatelessCsrfFilter.java), enables JWT resource server authentication, and maps roles into local authorities through [RoleAuthorizationAuthorityMapper.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/auth/RoleAuthorizationAuthorityMapper.java).

If multitenancy is enabled, [HttpTenantFilter.java](../src/main/java/de/caritas/cob/consultingtypeservice/filter/HttpTenantFilter.java) calls [TenantResolver.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/tenant/TenantResolver.java). The resolver reads tenantId from the request header or derives a subdomain through SubdomainExtractor and calls [TenantService.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/service/TenantService.java).

### Consulting Type Domain Layer

[ConsultingTypeService.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/service/ConsultingTypeService.java) is the central application service. It reads through ConsultingTypeRepositoryService, maps schema model data into basic, extended, and full responses, creates new consulting types, and applies patches. Limited patch permission cannot change language formal in single-domain mode and cannot change video-call settings.

[ConsultingTypeLoader.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/consultingtypes/ConsultingTypeLoader.java) loads [consulting-type-settings](../consulting-type-settings) at startup. It calls [ConsultingTypeValidator.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/consultingtypes/ConsultingTypeValidator.java), deserializes JSON into generated schema model objects, sets technical tenant context 0, and writes through the repository service.

### Topic And Settings Layer

[TopicServiceFacade.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/service/TopicServiceFacade.java) coordinates validation, sanitization, conversion, not-found behavior, and persistence delegation. Topic admin methods are feature-gated by [TopicFeatureAssertionAspect.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/admin/controller/TopicFeatureAssertionAspect.java), which calls [TopicFeatureAuthorisationService.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/service/TopicFeatureAuthorisationService.java).

Application settings are handled by [ApplicationSettingsServiceFacade.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/service/ApplicationSettingsServiceFacade.java), [ApplicationSettingsService.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/service/ApplicationSettingsService.java), [ApplicationSettingsConverter.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/service/ApplicationSettingsConverter.java), and [DefaultApplicationSettingsInitializer.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/service/DefaultApplicationSettingsInitializer.java).

## Main Data Flows

Consulting type read: OpenAPI contract -> ConsultingTypeController -> ConsultingTypeService -> ConsultingTypeRepositoryService -> MongoDB -> response mappers.

Consulting type startup load: consulting-type-settings JSON -> ConsultingTypeLoader -> ConsultingTypeValidator -> ConsultingTypeRepositoryService -> MongoDB.

Topic admin mutation: TopicAdminController -> method authorization -> TopicFeatureAssertionAspect -> TopicServiceFacade -> TopicService -> TopicRepository -> MariaDB.

Application settings: ApplicationSettingsController -> ApplicationSettingsServiceFacade -> ApplicationSettingsService -> ApplicationSettingsRepository -> MongoDB.

## Authentication And Tenant Flow

The request security path is StatelessCsrfFilter, Spring JWT validation, role extraction, RoleAuthorizationAuthorityMapper, optional HttpTenantFilter, TenantResolver, and then controller authorization. Tenant-aware Mongo repositories use TenantContext; JPA uses explicit tenant queries and Hibernate tenant filtering.

## API Structure

See [README.md](README.md#api-structure) for endpoint groups. The graph includes endpoint nodes parsed from every OpenAPI file and routes them to handwritten controller files where operationIds could be matched.

## Persistence Model

The MongoDB side is schema-model oriented. [src/main/resources/schemas/consulting-type.json](../src/main/resources/schemas/consulting-type.json) and [src/main/resources/schemas/application-settings.json](../src/main/resources/schemas/application-settings.json) generate Java model classes. ConsultingTypeEntity and ApplicationSettingsEntity extend those generated models and add Spring Data Mongo mapping.

The MariaDB side is conventional JPA. TopicEntity and TopicGroupEntity are entities, with topic groups related to topics through topic_group_x_topic. Changelogs in [src/main/resources/db/changelog](../src/main/resources/db/changelog) show the history from single-language topic fields to multilingual title columns.

## Deployment And Configuration

Runtime config is property-driven and environment-backed. Key settings include SPRING_DATASOURCE_URL, SPRING_DATA_MONGODB_URI, SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI, KEYCLOAK_AUTH_SERVER_URL, and TENANT_SERVICE_API_URL. The properties state that database schemas are managed separately in ORISO-Database, while this repo still carries Liquibase changelogs.

## Findings

See [FINDINGS.md](FINDINGS.md) and [DEPENDENCY-AUDIT.md](DEPENDENCY-AUDIT.md).

## Generated Artifacts

The dashboard graph is [knowledge-graph.json](knowledge-graph.json). Mermaid files live in [visuals](visuals).
