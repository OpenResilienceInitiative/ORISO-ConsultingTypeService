# ORISO Ecosystem Connection

## Navigation

- [Role In The Ecosystem](#role-in-the-ecosystem)
- [Incoming Consumers](#incoming-consumers)
- [Outgoing Dependencies](#outgoing-dependencies)
- [Data Ownership](#data-ownership)
- [Deployment Relationships](#deployment-relationships)
- [Change Coordination](#change-coordination)

## Role In The Ecosystem

ORISO-ConsultingTypeService is the platform authority for consulting type configuration, topic taxonomy, topic groups, and global application settings. It is a catalog and configuration service that other ORISO applications read to decide what consulting types exist, which fields are required, which roles are allowed, and which tenant/topic features are enabled.

## Incoming Consumers

The legacy [readme.md](../readme.md) says consulting type setting changes require restarting AgencyService and UserService because those services cache the settings. That makes this service a source of data consumed by ORISO-Frontend, ORISO-AgencyService, ORISO-UserService, and admin-console clients.

## Outgoing Dependencies

- ORISO-TenantService: called by [TenantService.java](../src/main/java/de/caritas/cob/consultingtypeservice/api/service/TenantService.java), generated from [services/tenantservice.yaml](../services/tenantservice.yaml).
- Keycloak: JWT issuer and role source for Spring Security.
- MongoDB: stores consulting_types and application_settings.
- MariaDB: stores topic, topic_group, and topic_group_x_topic.
- ORISO-Database: runtime properties say database schemas are managed separately there, even though this repo still carries Liquibase changelogs.

## Data Ownership

This service owns consulting type catalog shape and seed content, consulting type JSON schema, application settings schema, topic and topic group API behavior, and JPA entity mapping. It depends on other services for tenant identity, tenant settings, JWT issuer behavior, role assignments, and production database schema deployment conventions.

## Deployment Relationships

Runtime configuration expects service discovery or explicit environment variables for MariaDB, MongoDB, Keycloak, and TenantService. The comments in [application.properties](../src/main/resources/application.properties) prefer Kubernetes DNS names and warn against hardcoded cluster IPs.

The Docker image exposes 8080 while Spring config defaults to 8083. Deployment manifests should make that mapping explicit.

## Change Coordination

Coordinate changes when consulting type settings or schemas change, when topic feature logic changes, when roles or authorities change, when topic tables change, and when public OpenAPI contracts change. These are the areas most likely to affect frontend clients, AgencyService, UserService, TenantService, Keycloak configuration, or ORISO-Database.
