# Local Development

This setup runs ConsultingTypeService locally on `localhost:8083`, uses the remote ORISO dev
MariaDB/MongoDB instances through NodePorts, and allows the frontend on `localhost:9001` plus the
local Tenant/User/Agency services to call it directly.

## 1. Java

Use Java 17 if available:

```bash
/usr/libexec/java_home -V
```

The local run script auto-detects Java 11 first because the service targets Java 11, then falls back
to the default local JVM. Lombok is pinned in `pom.xml` so the service can compile on newer local
JDKs.

## 2. Create `config.env`

Copy the sample file and fill the `CHANGE_ME` values. `config.env` is ignored by git and should not
be committed.

```bash
cp config.env.example config.env
```

Important values for the local integrated setup:

```env
KEYCLOAK_AUTH_SERVER_URL=https://auth.oriso-dev.site
KEYCLOAK_REALM=online-beratung
KEYCLOAK_CONFIG_ADMIN_USERNAME=technical
KEYCLOAK_CONFIG_ADMIN_PASSWORD=CHANGE_ME
KEYCLOAK_CONFIG_ADMIN_CLIENT_ID=admin-cli
KEYCLOAK_CONFIG_APP_CLIENT_ID=app
KEYCLOAK_CORS=true
SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI=https://auth.oriso-dev.site/realms/online-beratung
SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI=https://auth.oriso-dev.site/realms/online-beratung/protocol/openid-connect/certs

LOCAL_JAVA_TRUSTSTORE=$HOME/.oriso/dev-truststore.jks
LOCAL_JAVA_TRUSTSTORE_PASSWORD=changeit

SPRING_DATASOURCE_URL=jdbc:mariadb://46.224.170.69:CHANGE_ME/consultingtypeservice
SPRING_DATASOURCE_USERNAME=consultingtypeservice
SPRING_DATASOURCE_PASSWORD=CHANGE_ME
SPRING_LIQUIBASE_ENABLED=false
SERVER_SERVLET_CONTEXT_PATH=/service
APP_BASE_URL=http://localhost:8083

SPRING_DATA_MONGODB_URI=mongodb://46.224.170.69:CHANGE_ME/consultingtypeservice?retryWrites=false

TENANT_SERVICE_API_URL=http://localhost:8081/service

MULTITENANCY_ENABLED=false
FEATURE_MULTITENANCY_WITH_SINGLE_DOMAIN_ENABLED=true
SETTING_MAIN_TENANT_SUBDOMAIN_FOR_SINGLE_DOMAIN_MULTITENANCY=caritas-berlin

CONSULTING_TYPE_CORS_ENABLED=true
# Must be explicit origins. Do not use * because credentialed CORS requests are enabled.
CONSULTING_TYPE_CORS_ALLOWED_ORIGINS=http://localhost:9001,http://127.0.0.1:9001
CONSULTING_TYPE_CORS_ALLOWED_PATHS=/**
```

`SERVER_SERVLET_CONTEXT_PATH=/service` means callers must use:

```text
http://localhost:8083/service
```

Use the NodePort values from the dev cluster for the MariaDB and MongoDB `CHANGE_ME` ports.

## 3. Run

```bash
./run-local-remote-db.sh
```

Expected local base URL:

```text
http://localhost:8083/service
```

## 4. Useful Checks

Check whether ConsultingTypeService is listening:

```bash
lsof -nP -iTCP:8083 -sTCP:LISTEN
```

Check health:

```bash
curl http://localhost:8083/service/actuator/health
```

Check consulting type data:

```bash
curl http://localhost:8083/service/consultingtypes/basic
```

Check frontend CORS:

```bash
curl -i -H 'Origin: http://localhost:9001' \
  http://localhost:8083/service/consultingtypes/basic
```

Stop the service with `Ctrl+C` in the terminal running the app.

## Local Service Pairing

When all four backend services and the frontend run locally, use these values:

```env
# TenantService
CONSULTING_TYPE_SERVICE_API_URL=http://localhost:8083/service
USER_SERVICE_API_URL=http://localhost:8082/service

# UserService
AGENCY_SERVICE_API_URL=http://localhost:8084/service/agencies
CONSULTING_TYPE_SERVICE_API_URL=http://localhost:8083/service
TENANT_SERVICE_API_URL=http://localhost:8081/service

# AgencyService
TENANT_SERVICE_API_URL=http://localhost:8081/service
CONSULTING_TYPE_SERVICE_API_URL=http://localhost:8083/service

# Frontend
REACT_APP_USER_SERVICE_ORIGIN=http://localhost:8082
REACT_APP_TENANT_SERVICE_ORIGIN=http://localhost:8081
REACT_APP_AGENCY_SERVICE_ORIGIN=http://localhost:8084
REACT_APP_CONSULTING_TYPE_SERVICE_ORIGIN=http://localhost:8083
```

Start order:

```text
TenantService -> ConsultingTypeService -> UserService -> AgencyService -> Frontend
```

The frontend origin does not include `/service`; frontend endpoint construction appends
`/service/...` internally.
