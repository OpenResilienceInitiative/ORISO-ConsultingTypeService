# AGENTS.md — ORISO-ConsultingTypeService

Load workspace parent `../AGENTS.md` first (`PROJECT_ORISO_ROOT` = parent of this repo).

## Stack

Java **21**, Spring Boot **4.0.1**, Maven Wrapper **3.9.15**. Owns consulting types and topics (`/service/consultingtypes`, `/service/topic*`).

## Commands

```bash
./mvnw -B test
./mvnw -B package -DskipTests
./mvnw -B spotless:check
./mvnw -B checkstyle:check
```

From workspace: `REPO=ORISO-ConsultingTypeService ../scripts/harness/verify-fast.sh` (or `verify-full.sh`).

CI: `./mvnw -B test` then `./mvnw -B package -DskipTests` on Java 21. Service docs live under `documentation/` (no root README).

## Context

- Integration branch: `pre-dev` when used for ORISO feature work.
- Keep type/topic contracts aligned with Admin and Frontend consumers.
- Secrets: use `config.env.example`; never commit real env files.

## Done

Touched tests pass; package succeeds for PR-bound work; format/checkstyle clean on touched Java. Task notes: `docs/agent-tasks/YYYY-MM-DD_short-name/` if needed.
