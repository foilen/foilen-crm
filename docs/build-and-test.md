# Build, Test, and Lint Commands

## Full Project Build (Backend + Frontend)

```bash
./gradlew build              # Full build with tests
./gradlew build test         # Explicit: build and test
./step-compile.sh            # Helper script for full build
./gradlew bootJar            # Create deployable JAR
./gradlew bootRun            # Run application locally
```

## Backend Only

```bash
./gradlew test               # Run all tests
./gradlew test --tests ClassName                    # Run specific test class
./gradlew test --tests ClassName.testMethodName     # Run single test method
./gradlew clean              # Clean build artifacts
```

## Frontend Only (in src/main/ui/)

```bash
npm start                    # Dev server on port 3000
npm run build               # Production build
npm run watch               # Watch mode for development
npm test                    # Run tests in watch mode
npm run test:ci             # Run tests once (CI mode)
```

## Database

No setup needed: for the `LOCAL` and `JUNIT` profiles, an embedded, ephemeral MongoDB (single-node replica set) is
started in-process automatically (via `de.flapdoodle.embed.mongo`), so `./gradlew bootRun` / running `CrmApp` in the
IDE / `./gradlew test` all just work. Data does not persist across restarts (fake data is reloaded on every boot).

## Email

- `LOCAL` profile must send real email via `EmailServiceSpring` (configured in `CrmMailConfig`, using the SMTP
  creds in `test-config.json`) — do NOT use `EmailServiceMock`.
- `EmailServiceMock` (in `com.foilen.crm.localonly`) is `JUNIT`-only: in `CrmSpringConfig`, its `@Bean` method
  carries its own method-level `@Profile("JUNIT")` narrowing the enclosing class-level `@Profile({"JUNIT", "LOCAL"})`
  (Spring ANDs the two), while `fakeDataService()` / `localLaunchService()` in that same class still apply to both.
