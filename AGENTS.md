# Coding Agent Guidelines for foilen-crm

This document provides essential information for AI coding agents working on the foilen-crm project. Detailed
guidance lives under `docs/` — read the relevant file(s) before working in that area.

## Project Overview

**Stack**: Java 25 + Spring Boot 4.1 (Backend) + React 19 + Vite 8 (Frontend)  
**Database**: MongoDB with Spring Data MongoDB  
**Build Tools**: Gradle 9.x (Backend), Vite 8 (Frontend)  
**Testing**: JUnit 6 (Backend), Vitest (Frontend)  
**Authentication**: Email + password, or a one-time code emailed to the user (session-based, Spring Security)

## Documentation Index

- `docs/build-and-test.md` — build/test/run commands, embedded MongoDB, email (LOCAL vs JUNIT) setup
- `docs/backend-style.md` — Java file structure, imports, naming, service/repository/controller patterns, error handling
- `docs/frontend-style.md` — React file structure, imports, naming, component/service patterns, i18n
- `docs/testing.md` — JUnit 6 (backend) and Vitest (frontend) conventions; do NOT run the test suite yourself
- `docs/patterns.md` — entitlement checks, DB entity conventions, form validation, i18n, pagination
- `docs/workflow.md` — local dev workflow and key file reference
- `docs/upgrading-dependencies.md` — steps to upgrade Java and Javascript dependencies

## Critical Notes

- First user account ever created is automatically admin
- CSRF protection enabled - use `service.js`, not axios directly
- Production MongoDB must run as a (single-node at minimum) replica set — `@Transactional` uses
  `MongoTransactionManager`, which requires one
- Swagger UI: `/swagger-ui/index.html`
- Supported languages: EN, FR
- When making changes, ensure docs/ and README.md are updated to reflect any modifications
