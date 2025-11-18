# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Foilen CRM is a simple, single-company CRM system built with Spring Boot (backend) and React (frontend). It manages clients, invoices, technical support contracts, recurrent items, and transactions.

## Build System & Dependencies

The project uses **Gradle** as its build system with Node.js integration for the frontend.

- Java 21 (source compatibility)
- Spring Boot 3.5.0
- React 19.1.1
- Node.js 22.16.0 (automatically downloaded by Gradle)

## Common Development Commands

### Building & Testing

```bash
# Full build with tests
./gradlew build test

# Build without tests
./gradlew build -x test

# Run only tests
./gradlew test

# Clean and build
./gradlew clean build
```

### Running Tests

```bash
# Run all tests
./gradlew test

# Run a specific test class
./gradlew test --tests "com.foilen.crm.services.ClientServiceImplTest"

# Run tests in CI mode (no watch)
cd src/main/ui && npm run test:ci
```

### Frontend Development

```bash
# Start frontend dev server with HMR (hot module replacement)
cd src/main/ui && npm start
# Access at http://localhost:3000 (proxies API calls to backend at :8080)

# Build frontend in watch mode (auto-rebuilds on changes)
./gradlew npmRunWatch

# Build frontend only
cd src/main/ui && npm run build

# Run frontend tests with Vitest
cd src/main/ui && npm test

# Run frontend tests in CI mode
cd src/main/ui && npm run test:ci
```

### Local Development

```bash
# Start MariaDB in Docker
./mariadb-start.sh

# Stop MariaDB
docker stop mariadb-crm mariadb-crm_phpmyadmin

# Run the application (in IntelliJ or via Gradle)
# Requires: test-config.json with Azure OAuth config
./gradlew bootRun
```

### Docker Testing

```bash
# Full Docker test (starts DB, builds app, runs container)
./test-crm-test.sh

# Access at http://127.0.0.1:8080/
# Cleanup: docker stop crm_db crm_db_phpmyadmin
```

## Architecture

### Backend (Spring Boot)

**Main Entry Point**: `CrmApp.java` - Runs database upgrades first, then starts the main application.

**Spring Configuration Classes**:
- `CrmSpringConfig`: Main configuration, component scanning, Freemarker, i18n
- `CrmDbLiveSpringConfig`: Database and JPA configuration
- `CrmWebSpringConfig`: Web MVC, interceptors, REST API
- `CrmSecuritySpringConfig`: OAuth2/Azure AD authentication
- `CrmUpgradesSpringConfig`: Database migration system

**Database Upgrade System**: The application uses Foilen's upgrader pattern. On startup, it runs all upgrade tasks in `src/main/java/com/foilen/crm/upgrades/` sequentially. New upgrades should follow the naming pattern `VYYYYMMDDNN_Description.java`.

**Layered Architecture**:
- **Entities**: `src/main/java/com/foilen/crm/db/entities/` - JPA entities
- **DAOs**: `src/main/java/com/foilen/crm/db/dao/` - Spring Data JPA repositories
- **Services**: `src/main/java/com/foilen/crm/services/` - Business logic layer
- **Controllers**: `src/main/java/com/foilen/crm/web/controller/` - REST API endpoints
- **Web Models**: `src/main/java/com/foilen/crm/web/model/` - DTOs for API responses

**Key Services**:
- `ClientService`: Client management
- `ItemService`: Invoice items (billable/non-billable)
- `TransactionService`: Financial transactions
- `RecurrentItemService`: Recurring billing items
- `TechnicalSupportService`: Support contracts
- `ReportService`: Financial reporting
- `EntitlementService`: Permission checks (admin vs regular user)

**Authentication**: Uses Azure AD OAuth2. First user to log in becomes admin. Admins can manage the full system; regular users have limited access.

### Frontend (React)

**Location**: `src/main/ui/`

**Build Tool**: Uses **Vite** for fast builds and hot module replacement

**Structure**:
- `src/components/`: Reusable components (ClientSelect, Pagination, etc.) [.jsx files]
- `src/views/`: Page components (ClientsList, ItemsList, etc.) [.jsx files]
- `src/utils/`: Utilities (http, translations, features) [.js files]
- `src/App.jsx`: Main application with routing and navbar
- `vite.config.js`: Vite configuration with proxy settings for backend API
- `vitest.config.js`: Testing configuration using Vitest

**Build Integration**: Gradle automatically runs `npm install` and `npm run build` (Vite), which outputs directly to `build/resources/main/static` for inclusion in the Spring Boot JAR.

**API Communication**: Uses axios with utilities in `src/main/ui/src/utils/http.js`. CSRF protection is handled automatically.

**Internationalization**: Supports English and French. Translations loaded from backend at startup and managed via `TranslationUtils.js`.

### Configuration

**Runtime Config**: JSON file mapped to `CrmConfig.java` with:
- Database connection (MySQL/MariaDB)
- Email/SMTP settings
- Azure AD OAuth credentials
- Company name and base URL
- Cookie signature salt
- Optional email template directory

**Environment Overrides**:
- `HTTP_PORT`: Server port (default 8080)
- `CONFIG_FILE`: Path to config JSON
- `MYSQL_PORT_3306_TCP_ADDR`: Override MySQL hostname
- `MYSQL_PORT_3306_TCP_PORT`: Override MySQL port

## Testing

### Backend Tests

- All tests extend `AbstractSpringTests` in `src/test/java/com/foilen/crm/test/`
- Uses JUnit 5 (migrated from JUnit 4)
- Test profile: `JUNIT`
- Database: Separate test database `foilen_crm_junits` created by `mariadb-start.sh`
- Fake data generation available via `FakeDataService`

**Test utilities**:
- `expectNotAdmin()`: Assert non-admin permission errors
- `trimClient()`, `trimItem()`, etc.: Convert entities to web models for assertions

### Frontend Tests

Located in `src/main/ui/src/`, using React Testing Library and Jest.

## Text Messages & Translations

Translations stored in:
- `src/main/resources/com/foilen/crm/messages/messages_en.properties`
- `src/main/resources/com/foilen/crm/messages/messages_fr.properties`

**Sorting translations**: Run `SortPropertiesApp` to alphabetically sort message files.

## API Documentation

Swagger UI available at: `http://127.0.0.1:8080/swagger-ui/index.html` when running locally.

## Key Patterns & Conventions

1. **Service interfaces**: All services have an interface (e.g., `ClientService`) and implementation (e.g., `ClientServiceImpl`)
2. **Exception handling**: Use `ErrorMessageException` for user-facing errors with i18n message keys
3. **Permissions**: Check via `EntitlementService.isAdmin()` - throw `ErrorMessageException("error.notAdmin")` for unauthorized access
4. **DAO naming**: Spring Data JPA repositories named `*Dao` (not Repository)
5. **Component scanning**: Services, DAOs, and tasks are auto-discovered via `@ComponentScan` in `CrmSpringConfig`

## Development Profiles

- `PROD`: Production (default when running via `CrmApp`)
- `JUNIT`: Test environment (uses `EmailServiceMock`, `FakeDataService`)
- `LOCAL`: Local development (same as JUNIT, can be run via IDE)

## Skip Node.js Build

Set environment variable `SKIP_NODEJS=true` to skip frontend build during Gradle tasks (useful for backend-only development).
