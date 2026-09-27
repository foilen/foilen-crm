# Development Workflow

1. No database setup needed (embedded MongoDB starts in-process for the `LOCAL`/`JUNIT` profiles)
2. Configure `test-config.json` (mail server credentials are needed to send login-code emails)
3. Run backend: `./gradlew bootRun` OR run `CrmApp.java` in IDE
4. Run frontend dev server: `cd src/main/ui && npm start`
5. Access: `http://localhost:8080` (backend) or `http://localhost:3000` (frontend dev)

# Key Files Reference

- **Backend Entry**: `src/main/java/com/foilen/crm/CrmApp.java`
- **Frontend Entry**: `src/main/ui/src/index.jsx`
- **Base Service**: `src/main/java/com/foilen/crm/services/AbstractApiService.java`
- **Base Test**: `src/test/java/com/foilen/crm/test/AbstractSpringTests.java`
- **Frontend Service/HTTP layer**: `src/main/ui/src/service.js`
- **Translation Utils**: `src/main/ui/src/utils/TranslationUtils.js`
- **Mail Config**: `src/main/java/com/foilen/crm/CrmMailConfig.java`
- **Spring Config (profiles, mocks)**: `src/main/java/com/foilen/crm/CrmSpringConfig.java`
