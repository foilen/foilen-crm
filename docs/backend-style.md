# Java (Backend) Code Style Guidelines

## File Structure

```
src/main/java/com/foilen/crm/
  ├── db/repository/       # Spring Data MongoDB repositories
  ├── db/entities/         # MongoDB @Document classes, grouped by sub-domain
  │     ├── invoice/       #   Client, Item, RecurrentItem, TechnicalSupport, Transaction
  │     └── user/          #   User
  ├── services/            # Business logic (interfaces + implementations)
  ├── web/controller/      # REST API controllers
  ├── web/model/           # DTOs and API request/response models
  ├── web/interceptor/     # Servlet filters/interceptors (e.g. ProcessUserInterceptor)
  ├── exception/           # Custom exceptions
  ├── tasks/               # Scheduled tasks
  ├── upgrades/            # One-off Mongo migrations (V_YYYYMMDD_NN_Description), run at boot
  └── localonly/           # LOCAL/JUNIT-only beans: FakeDataService, EmailServiceMock,
                            # EmbeddedMongoDbSpringConfig, LocalLaunchService
```

## Imports Ordering

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

## Naming Conventions

- **Classes**: PascalCase (`ClientServiceImpl`, `ClientApiController`)
- **Interfaces**: PascalCase without "I" prefix (`ClientService`, `ClientRepository`)
- **Methods**: camelCase (`listAll`, `validateMandatory`)
- **Constants**: UPPER_SNAKE_CASE (`VALID_LANGS`)
- **Packages**: lowercase (`com.foilen.crm.services`)

## Service Layer Pattern (Critical)

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

## Error Handling

- **Validation errors**: Use `FormResult` with field-specific errors
- **Authorization errors**: Throw exceptions via `entitlementService.can*OrFail(userId)` methods
- **System errors**: Throw `ErrorMessageException` with i18n message keys
- **Validation helpers**: Use inherited methods from `AbstractApiService`:
    - `validateMandatory(formResult, fieldName, value)`
    - `validateEmail(formResult, fieldName, value)`
    - `validateUnique(formResult, fieldName, dao, field, value)`

## Repository Pattern

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

## Controller Pattern

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

## REST API Endpoints Convention

- Pattern: `/api/{resource}/{action}`
- Create: `POST /api/client`
- List: `GET /api/client/listAll`
- Update: `PUT /api/client/{id}`
- Delete: `DELETE /api/client/{id}`
