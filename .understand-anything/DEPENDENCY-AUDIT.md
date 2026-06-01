# ORISO-ConsultingTypeService Dependency Audit

## Navigation

- [Build Stack](#build-stack)
- [Runtime Dependencies](#runtime-dependencies)
- [Code Generation](#code-generation)
- [Risk Register](#risk-register)
- [Actionable Cleanup](#actionable-cleanup)

## Build Stack

[pom.xml](../pom.xml) declares Spring Boot 4.0.1, Java 21, Spring Cloud 2025.0.0, Maven jar packaging, and build-time code generation for OpenAPI and JSON Schema. The Docker runtime does not currently match this stack because [Dockerfile](../Dockerfile) uses Java 11.

## Runtime Dependencies

- Spring Boot web, security, validation, OAuth2 resource server, HATEOAS, actuator, cache, JPA, and MongoDB starters.
- MariaDB JDBC driver and H2 for test/local paths.
- Keycloak admin client 24.0.5.
- Ehcache 2.10.9.2 through net.sf.ehcache.
- Micrometer tracing bridge and Zipkin exporter are present but tracing is disabled by default.
- OWASP Java HTML Sanitizer for topic input cleaning.

## Code Generation

- OpenAPI Generator 6.2.1 creates Spring controller interfaces and DTOs from [api](../api).
- OpenAPI Generator also creates a Java RestTemplate client from [services/tenantservice.yaml](../services/tenantservice.yaml).
- jsonschema2pojo 0.4.34 generates schema model classes from [src/main/resources/schemas](../src/main/resources/schemas).

## Risk Register

| Area | Evidence | Risk |
| --- | --- | --- |
| Java runtime | pom.xml Java 21 vs Dockerfile Java 11 | Built jar may fail at container startup. |
| Springdoc | springdoc.version=2.0.0 with Spring Boot 4 | Compatibility should be verified. |
| OpenAPI Generator | 6.2.1 | Generated Spring/Jakarta output can lag current Spring Boot conventions. |
| Ehcache | net.sf.ehcache 2.10.9.2 | Ehcache 2 is legacy and can create migration friction. |
| Liquibase | plugin 4.1.1, core 4.9.1, starter present but disabled | Migration ownership and versioning are unclear. |
| commons-io | 2.8.0 | Old utility dependency; verify advisories and managed version conflicts. |
| org.json | 20201115 | Old JSON dependency; often appears in scanners. |
| Everit JSON Schema | 1.12.2 | Validator dependency is present, but validation is disabled. |
| Checkstyle | failsOnError=false and failOnViolation=false | Style regressions cannot block builds. |
| Surefire config | Global skipTests=true, executions re-enable test groups | Direct plugin usage can surprise developers. |

## Actionable Cleanup

1. Align Docker runtime with Java 21 or intentionally lower the Maven bytecode target.
2. Re-enable consulting type JSON validation or remove the dead validator path and document why validation is externalized.
3. Decide whether Liquibase belongs in this repo or ORISO-Database, then make build/runtime config reflect that decision.
4. Review Spring Boot 4 compatibility for Springdoc, OpenAPI Generator, Keycloak admin client, Ehcache 2, and jsonschema2pojo.
5. Remove unused Docker settings and decide whether JDWP should be on by default.
6. Make Checkstyle fail the build once the migration state is clean enough.
