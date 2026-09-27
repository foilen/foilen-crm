# Important Patterns and Conventions

## Entitlement Checks

ALWAYS check user permissions in service methods before performing operations:

```java
entitlementService.canCreateClientOrFail(userId);
entitlementService.canEditClientOrFail(userId, clientId);
```

## Database Entities

- Use Spring Data MongoDB annotations (`@Document`, `@Id`) — keep them annotation-light otherwise
- No `@DBRef`: relations to other documents are plain `String` id fields (e.g. `clientId`), resolved explicitly via the
  referenced repository
- Builder pattern for fluent setters returning `this`
- No optimistic locking (`@Version`) — MongoDB writes are per-document atomic
- Never use `@Indexed`/`@CompoundIndex` annotations on entities: create and drop MongoDB indexes only through
  `V_YYYYMMDD_NN_Description` upgrade tasks in `upgrades/` (see `com.foilen.smalltools.upgrader.trackers.AbstractMongoUpgradeTask`'s
  `addCollection`/`addIndex`/`dropIndex`), e.g. `V_20260725_01_Client_CollectionAndIndexes`. This keeps index
  changes tracked, ordered, and applied automatically at boot instead of relying on annotation scanning.

## Form Validation

- Use `FormResult` for returning validation errors
- Validate in service layer, not controller
- Return field-specific errors: `formResult.addError("fieldName", "error.key")`

## Internationalization

- Backend: Add keys to `messages_en.properties` and `messages_fr.properties`
- Frontend: Use `t('key')` function from TranslationUtils
- Format: `key=value` with `{0}`, `{1}` placeholders

## Pagination

- Backend: Use Spring's `Pageable` and `Page<T>`
- Frontend: `pageId` is 1-based; track the `pagination` object (`currentPageUi`, `totalPages`, `firstPage`,
  `lastPage`) returned by the API in component state and pass it to `PaginationControl`
- API responses include pagination metadata
