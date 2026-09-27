# Testing Guidelines

When code changes affect behavior, update the relevant tests (new/changed test methods, JSON assertion files, etc.)
as part of the change. Do NOT attempt to run the test suite (`./gradlew test`, `npm test`, `npm run test:ci`) —
it fails in this sandbox environment regardless of correctness, so a failed run is not signal. Leave running the
tests to the user.

## Backend Tests (JUnit 6)

### Test Structure

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

### Test Data

- Inherit from `AbstractSpringTests` at `src/test/java/com/foilen/crm/test/AbstractSpringTests.java`
- Use `FakeDataService` for test data setup
- JSON assertion files: `src/test/resources/com/foilen/crm/services/{TestClass}-{testMethod}.json`
- Profile: `@ActiveProfiles("JUNIT")`

## Frontend Tests (Vitest)

- Framework: Vitest with jsdom environment
- Setup file: `src/main/ui/src/setupTests.js`
- File naming: `*.test.jsx` or `*.spec.jsx`
- Currently minimal frontend tests - follow React Testing Library patterns when adding
