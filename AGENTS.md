# Coding Agent Guidelines for foilen-crm

This document provides essential information for AI coding agents working on the foilen-crm project.

## Project Overview

**Stack**: Java 25 + Spring Boot 4.1 (Backend) + React 19 + Vite 8 (Frontend)  
**Database**: MongoDB with Spring Data MongoDB  
**Build Tools**: Gradle 7.x (Backend), Vite (Frontend)  
**Testing**: JUnit 5 (Backend), Vitest (Frontend)  
**Authentication**: Azure Active Directory OAuth2

## Build, Test, and Lint Commands

### Full Project Build (Backend + Frontend)

```bash
./gradlew build              # Full build with tests
./gradlew build test         # Explicit: build and test
./step-compile.sh            # Helper script for full build
./gradlew bootJar            # Create deployable JAR
./gradlew bootRun            # Run application locally
```

### Backend Only

```bash
./gradlew test               # Run all tests
./gradlew test --tests ClassName                    # Run specific test class
./gradlew test --tests ClassName.testMethodName     # Run single test method
./gradlew clean              # Clean build artifacts
```

### Frontend Only (in src/main/ui/)

```bash
npm start                    # Dev server on port 3000
npm run build               # Production build
npm run watch               # Watch mode for development
npm test                    # Run tests in watch mode
npm run test:ci             # Run tests once (CI mode)
```

### Database

No setup needed: for the `LOCAL` and `JUNIT` profiles, an embedded, ephemeral MongoDB (single-node replica set) is
started in-process automatically (via `de.flapdoodle.embed.mongo`), so `./gradlew bootRun` / running `CrmApp` in the
IDE / `./gradlew test` all just work. Data does not persist across restarts (fake data is reloaded on every boot).

## Code Style Guidelines

### Java (Backend)

#### File Structure

```
src/main/java/com/foilen/crm/
  ├── db/repository/       # Spring Data MongoDB repositories
  ├── db/entities/         # MongoDB @Document classes
  ├── services/            # Business logic (interfaces + implementations)
  ├── web/controller/      # REST API controllers
  ├── web/model/           # DTOs and API request/response models
  ├── exception/           # Custom exceptions
  └── tasks/               # Scheduled tasks
```

#### Imports Ordering

```java
// 1. Java standard library

import java.util.*;
// 2. Spring framework
import org.springframework.*;
// 3. Internal packages
import com.foilen.crm.*;
// 4. External libraries
import com.foilen.smalltools.*;
import com.google.common.base.*;
```

#### Naming Conventions

- **Classes**: PascalCase (`ClientServiceImpl`, `ClientApiController`)
- **Interfaces**: PascalCase without "I" prefix (`ClientService`, `ClientRepository`)
- **Methods**: camelCase (`listAll`, `validateMandatory`)
- **Constants**: UPPER_SNAKE_CASE (`VALID_LANGS`)
- **Packages**: lowercase (`com.foilen.crm.services`)

#### Service Layer Pattern (Critical)

```java

@Service
@Transactional
public class ClientServiceImpl extends AbstractApiService implements ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Override
    public FormResult create(String userId, CreateOrUpdateClientForm form) {
        FormResult formResult = new FormResult();

        // 1. ALWAYS check entitlements first
        entitlementService.canCreateClientOrFail(userId);

        // 2. Validate all mandatory fields
        validateMandatory(formResult, "name", form.getName());
        validateEmail(formResult, "email", form.getEmail());

        if (!formResult.isSuccess()) {
            return formResult;
        }

        // 3. Business logic
        Client entity = JsonTools.clone(form, Client.class);
        clientRepository.save(entity);

        return formResult;
    }
}
```

#### Error Handling

- **Validation errors**: Use `FormResult` with field-specific errors
- **Authorization errors**: Throw exceptions via `entitlementService.can*OrFail(userId)` methods
- **System errors**: Throw `ErrorMessageException` with i18n message keys
- **Validation helpers**: Use inherited methods from `AbstractApiService`:
    - `validateMandatory(formResult, fieldName, value)`
    - `validateEmail(formResult, fieldName, value)`
    - `validateUnique(formResult, fieldName, dao, field, value)`

#### Repository Pattern

```java

@Repository
public interface ClientRepository extends MongoRepository<Client, String>, ClientRepositoryCustom {
    Client findByShortName(String shortName);
}
```

Method-name-derived queries go directly on the `XxxRepository` interface. Anything Spring Data can't express that way
(regex search, `$lookup` aggregations, paginated aggregations) goes on a companion `XxxRepositoryCustom` interface,
implemented by `XxxRepositoryImpl extends AbstractRepositoryCustom` (see
`com.foilen.crm.db.repository.AbstractRepositoryCustom`
for the `find`/`aggregation`/`lookupByStringId` helpers). Relations between documents are plain `String` id fields (e.g.
`Item.clientId`), never `@DBRef` — resolve them explicitly via the referenced repository where needed.

#### Controller Pattern

```java

@RequestMapping(value = "api/resource", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
@RestController
@SwaggerExpose
public class ResourceApiController {

    @Autowired
    private ResourceService resourceService;

    @PostMapping
    public FormResult create(Authentication authentication, @RequestBody FormClass form) {
        return resourceService.create(authentication.getName(), form);
    }
}
```

#### REST API Endpoints Convention

- Pattern: `/api/{resource}/{action}`
- Create: `POST /api/client`
- List: `GET /api/client/listAll`
- Update: `PUT /api/client/{id}`
- Delete: `DELETE /api/client/{id}`

### JavaScript/React (Frontend)

#### File Structure

```
src/main/ui/src/
  ├── components/          # Reusable components
  ├── views/              # Page-level components
  └── utils/              # Utility functions (http, translations, etc.)
```

#### Imports Ordering

```javascript
// 1. React and hooks
import React, {useEffect, useState} from 'react'
// 2. Third-party libraries (routing, etc.)
import {NavLink, Route} from 'react-router-dom'
import axios from 'axios'
// 3. Local components
import ErrorResults from '../components/ErrorResults'
// 4. Utilities
import {t, updateAppDetails} from './utils/TranslationUtils'
// 5. CSS last
import './App.css'
```

#### Naming Conventions

- **Components**: PascalCase (`ClientsList`, `ErrorResults`)
- **Files**: Match component name with `.jsx` extension (`ClientsList.jsx`)
- **Functions**: camelCase (`refresh`, `handleCreateFormChange`)
- **Constants**: camelCase or UPPER_SNAKE_CASE for true constants

#### Component Pattern (CRUD)

```javascript
function ResourceList() {
    const [items, setItems] = useState([])
    const [pagination, setPagination] = useState({pageId: 0, totalPages: 1})
    const [createForm, setCreateForm] = useState({})
    const [editForm, setEditForm] = useState({})
    const [formResult, setFormResult] = useState({})

    const refresh = async (pageId = 0) => {
        try {
            const response = await get('/api/resource/listAll', {pageId})
            setItems(response.data.items || [])
            setPagination(response.data.pagination)
        } catch (error) {
            console.error('Error loading resources', error)
        }
    }

    useEffect(() => {
        refresh()
    }, [])

    return (/* JSX with modals + table */)
}
```

#### Error Handling

```javascript
// Use try/catch for async operations
try {
    const response = await post('/api/resource', form)
    showSuccess(t('prompt.create.success'))
    refresh()
} catch (error) {
    console.error('Error creating resource', error)
    setFormResult(error.response?.data || {})
}

// Display errors with ErrorResults component
<ErrorResults formResult={formResult}/>
```

#### HTTP Utilities

- Use functions from `src/main/ui/src/utils/http.js` (not axios directly)
- Available: `get(url, params)`, `post(url, data)`, `put(url, data)`, `del(url)`
- CSRF token handling is automatic via axios interceptors

#### Translation/i18n

```javascript
import {t} from './utils/TranslationUtils'

// Simple translation
{
    t('menu.clients')
}

// With placeholders
{
    t('prompt.create.success', {0: clientName})
}
```

## Testing Guidelines

### Backend Tests (JUnit 5)

#### Test Structure

```java

@DisplayName("Service Description")
public class ServiceImplTest extends AbstractSpringTests {

    @Nested
    @DisplayName("Feature Tests")
    class FeatureTests {

        @Test
        @DisplayName("Should succeed when conditions are met")
        void testMethod_OK() {
            // Arrange: Use FakeDataService for test data
            fakeDataService.createAdminUser(ADMIN_EMAIL);

            // Act
            FormResult result = service.method(ADMIN_EMAIL, form);

            // Assert
            AssertTools.assertJsonComparison(getClass(), "ServiceImplTest-testMethod_OK.json", result);
        }
    }
}
```

#### Test Data

- Inherit from `AbstractSpringTests` at `src/test/java/com/foilen/crm/test/AbstractSpringTests.java`
- Use `FakeDataService` for test data setup
- JSON assertion files: `src/test/resources/com/foilen/crm/services/{TestClass}-{testMethod}.json`
- Profile: `@ActiveProfiles("JUNIT")`

### Frontend Tests (Vitest)

- Framework: Vitest with jsdom environment
- Setup file: `src/main/ui/src/setupTests.js`
- File naming: `*.test.jsx` or `*.spec.jsx`
- Currently minimal frontend tests - follow React Testing Library patterns when adding

## Important Patterns and Conventions

### Entitlement Checks

ALWAYS check user permissions in service methods before performing operations:

```java
entitlementService.canCreateClientOrFail(userId);
entitlementService.

canEditClientOrFail(userId, clientId);
```

### Database Entities

- Use Spring Data MongoDB annotations (`@Document`, `@Id`) — keep them annotation-light otherwise
- No `@DBRef`: relations to other documents are plain `String` id fields (e.g. `clientId`), resolved explicitly via the
  referenced repository
- Builder pattern for fluent setters returning `this`
- No optimistic locking (`@Version`) — MongoDB writes are per-document atomic

### Form Validation

- Use `FormResult` for returning validation errors
- Validate in service layer, not controller
- Return field-specific errors: `formResult.addError("fieldName", "error.key")`

### Internationalization

- Backend: Add keys to `messages_en.properties` and `messages_fr.properties`
- Frontend: Use `t('key')` function from TranslationUtils
- Format: `key=value` with `{0}`, `{1}` placeholders

### Pagination

- Backend: Use Spring's `Pageable` and `Page<T>`
- Frontend: Track `pageId` and `totalPages` in component state
- API responses include pagination metadata

## Development Workflow

1. No database setup needed (embedded MongoDB starts in-process for the `LOCAL`/`JUNIT` profiles)
2. Configure `test-config.json` with Azure AD credentials (for auth)
3. Run backend: `./gradlew bootRun` OR run `CrmApp.java` in IDE
4. Run frontend dev server: `cd src/main/ui && npm start`
5. Access: `http://localhost:8080` (backend) or `http://localhost:3000` (frontend dev)

## Key Files Reference

- **Backend Entry**: `src/main/java/com/foilen/crm/CrmApp.java`
- **Frontend Entry**: `src/main/ui/src/index.jsx`
- **Base Service**: `src/main/java/com/foilen/crm/services/AbstractApiService.java`
- **Base Test**: `src/test/java/com/foilen/crm/test/AbstractSpringTests.java`
- **HTTP Utils**: `src/main/ui/src/utils/http.js`
- **Translation Utils**: `src/main/ui/src/utils/TranslationUtils.js`

## Critical Notes

- First user becomes admin automatically
- Azure AD authentication required for production
- CSRF protection enabled - use provided HTTP utilities
- Production MongoDB must run as a (single-node at minimum) replica set — `@Transactional` uses
  `MongoTransactionManager`, which requires one
- Swagger UI: `/swagger-ui/index.html`
- Supported languages: EN, FR
