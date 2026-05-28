# ORISO-ConsultingTypeService Understand-Anything Map

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

ORISO-ConsultingTypeService is a Spring Boot service that owns consulting type settings, topic taxonomy, topic groups, and global application settings for the ORISO platform. The service is OpenAPI-first: YAML files in [api/](../api) define the HTTP contracts, and [pom.xml](../pom.xml) generates controller interfaces, DTOs, schema models, and the TenantService client during the Maven build.

The repository intentionally mixes persistence technologies. Consulting type settings and application settings are MongoDB documents. Topics and topic groups are JPA entities backed by MariaDB tables and Liquibase changelogs. Runtime tenancy is optional and controlled by multitenancy.enabled; when active, request tenant resolution flows through HttpTenantFilter, TenantResolver, TenantContext, tenant-aware repositories, and Hibernate tenant filtering.

## Repository Map

- [api/](../api) - OpenAPI source contracts for consulting types, consulting type admin, topics, and application settings.
- [services/tenantservice.yaml](../services/tenantservice.yaml) - external TenantService contract used for generated RestTemplate client code.
- [src/main/java/de/caritas/cob/consultingtypeservice/api/controller](../src/main/java/de/caritas/cob/consultingtypeservice/api/controller) - public and admin REST controllers.
- [src/main/java/de/caritas/cob/consultingtypeservice/api/service](../src/main/java/de/caritas/cob/consultingtypeservice/api/service) - application services, facades, tenant client, settings initializer, and tenant write interceptor.
- [src/main/java/de/caritas/cob/consultingtypeservice/api/consultingtypes](../src/main/java/de/caritas/cob/consultingtypeservice/api/consultingtypes) - consulting type loading, validation, Mongo repository services, group repositories, converters, and role model.
- [src/main/java/de/caritas/cob/consultingtypeservice/api/model](../src/main/java/de/caritas/cob/consultingtypeservice/api/model) - Mongo documents and JPA entities.
- [src/main/resources/db/changelog](../src/main/resources/db/changelog) - topic and topic-group MariaDB changelogs.
- [consulting-type-settings/](../consulting-type-settings) - startup seed JSON files loaded into MongoDB.
- [src/test](../src/test) - unit and integration coverage for controllers, services, repositories, filters, mappers, and validation.

## Architecture Layers

1. API contract layer: [api/consultingtypeservice.yml](../api/consultingtypeservice.yml), [api/topicservice.yml](../api/topicservice.yml), [api/applicationsettingsservice.yml](../api/applicationsettingsservice.yml), [api/consultingtypeadminservice.yml](../api/consultingtypeadminservice.yml), and [services/tenantservice.yaml](../services/tenantservice.yaml).
2. HTTP layer: controllers implement generated interfaces and delegate to services or facades.
3. Security and tenancy layer: [SecurityConfig.java](../src/main/java/de/caritas/cob/consultingtypeservice/config/SecurityConfig.java), [RoleAuthorizationAuthorityMapper.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/auth/RoleAuthorizationAuthorityMapper.java), [HttpTenantFilter.java](../src/main/java/de/caritas/cob/consultingtypeservice/filter/HttpTenantFilter.java), [TenantResolver.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/tenant/TenantResolver.java), and [TenantHibernateInterceptor.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/service/TenantHibernateInterceptor.java).
4. Domain service layer: [ConsultingTypeService.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/service/ConsultingTypeService.java), [TopicServiceFacade.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/service/TopicServiceFacade.java), [TopicService.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/service/TopicService.java), [ApplicationSettingsServiceFacade.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/service/ApplicationSettingsServiceFacade.java), and [ApplicationSettingsService.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/service/ApplicationSettingsService.java).
5. Persistence layer: Mongo repositories for consulting types and settings, JPA repositories for topics and topic groups, plus Liquibase SQL changelogs.

## Main Data Flows

Consulting type reads start in [api/consultingtypeservice.yml](../api/consultingtypeservice.yml), route to [ConsultingTypeController.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/controller/ConsultingTypeController.java), call [ConsultingTypeService.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/service/ConsultingTypeService.java), read from ConsultingTypeRepositoryService, and map through the basic, extended, full, or group mappers.

Startup seed loading runs through [ConsultingTypeLoader.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/consultingtypes/ConsultingTypeLoader.java). It reads [consulting-type-settings](../consulting-type-settings), calls [ConsultingTypeValidator.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/consultingtypes/ConsultingTypeValidator.java), and persists through ConsultingTypeRepositoryService using technical tenant context 0.

Topic mutations route through [TopicAdminController.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/controller/TopicAdminController.java), pass method authorization, run TopicFeatureAssertionAspect, validate and sanitize in [TopicServiceFacade.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/service/TopicServiceFacade.java), and persist through [TopicService.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/service/TopicService.java) and [TopicRepository.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/repository/TopicRepository.java).

Application settings reads and patches route through [ApplicationSettingsController.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/controller/ApplicationSettingsController.java), [ApplicationSettingsServiceFacade.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/service/ApplicationSettingsServiceFacade.java), [ApplicationSettingsService.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/service/ApplicationSettingsService.java), and [ApplicationSettingsRepository.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/repository/ApplicationSettingsRepository.java).

## Authentication And Tenant Flow

The service uses Spring Security as a JWT resource server. [SecurityConfig.java](../src/main/java/de/caritas/cob/consultingtypeservice/config/SecurityConfig.java) extracts realm and resource roles, sends them to [RoleAuthorizationAuthorityMapper.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/auth/RoleAuthorizationAuthorityMapper.java), and exposes local AUTHORIZATION values to method authorization. tenant-admin, single-tenant-admin, and topic-admin are the meaningful local roles in [UserRole.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/auth/UserRole.java).

If multitenancy.enabled is true, [HttpTenantFilter.java](../src/main/java/de/caritas/cob/consultingtypeservice/filter/HttpTenantFilter.java) resolves the current tenant. [TenantResolver.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/tenant/TenantResolver.java) checks the tenantId header first, then derives a subdomain and calls [TenantService.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/service/TenantService.java).

## API Structure

- Consulting types: GET /consultingtypes/basic, GET /consultingtypes/{id}/basic, GET /consultingtypes/{id}/extended, GET /consultingtypes/{id}/full, GET /consultingtypes/byslug/{slug}/full, GET /consultingtypes/bytenant/{tenantId}/full, GET /consultingtypes/groups, POST /consultingtypes, PATCH /consultingtypes/{id}.
- Consulting type admin: GET /consultingtypeadmin and GET /consultingtypeadmin/consultingtypes.
- Topics: GET /topic, GET /topic/public, GET /topic-groups, POST /topicadmin, GET /topicadmin, GET /topicadmin/{id}, PATCH /topicadmin/{id}.
- Application settings: GET /settings and PATCH /settingsadmin.
- Service health/docs: /actuator/health, /consultingtypes/docs, /v3/api-docs.

## Persistence Model

- MongoDB consulting_types: [ConsultingTypeEntity.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/model/ConsultingTypeEntity.java), ConsultingTypeRepository, and tenant-aware or tenant-unaware repository services.
- MongoDB application_settings: [ApplicationSettingsEntity.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/model/ApplicationSettingsEntity.java) and [ApplicationSettingsRepository.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/repository/ApplicationSettingsRepository.java).
- MariaDB topic: [TopicEntity.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/model/TopicEntity.java), [TopicRepository.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/repository/TopicRepository.java), and the Liquibase changelog series.
- MariaDB topic_group and topic_group_x_topic: [TopicGroupEntity.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/model/TopicGroupEntity.java), [TopicGroupRepository.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/repository/TopicGroupRepository.java), and [topicGroups.sql](../src/main/resources/db/changelog/changeset/0004_topic_groups/topicGroups.sql).

## Deployment And Configuration

[pom.xml](../pom.xml) targets Java 21 and Spring Boot 4.0.1. [Dockerfile](../Dockerfile) currently uses eclipse-temurin:11-jre, copies ConsultingTypeService.jar, exposes 8080, and starts Java with remote debug enabled on port 5005. Runtime settings are mostly environment-backed through [application.properties](../src/main/resources/application.properties).

## Findings

The detailed findings are in [FINDINGS.md](FINDINGS.md). High-priority items are Java 21 build vs Java 11 Docker runtime, disabled consulting type JSON validation, duplicate/conflicting spring.jpa.hibernate.ddl-auto, and a tenant-aware slug lookup branch that appears to discard the technical-tenant lookup result.

## Generated Artifacts

- [knowledge-graph.json](knowledge-graph.json) - dashboard-ready Understand-Anything graph.
- [ARCHITECTURE.md](ARCHITECTURE.md) - detailed architecture notes.
- [ONBOARDING.md](ONBOARDING.md) - developer onboarding guide.
- [ORISO-ECOSYSTEM.md](ORISO-ECOSYSTEM.md) - ecosystem integration notes.
- [DEPENDENCY-AUDIT.md](DEPENDENCY-AUDIT.md) - dependency and build risk audit.
- [visuals/](visuals) - Mermaid architecture, auth, data, deployment, and ecosystem flows.
