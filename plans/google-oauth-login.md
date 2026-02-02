# Goal

Document the Google OAuth login feature (Spring Security OAuth2 login, Thymeleaf UI state for authenticated vs unauthenticated users, and local development redirect handling).

# Context

Project files involved:
- [`gradle/libs.versions.toml`](gradle/libs.versions.toml:1)
- [`build.gradle.kts`](build.gradle.kts:1)
- [`src/main/java/com/fde/google_drive_organizer/adapter/inbound/http/SecurityConfig.java`](src/main/java/com/fde/google_drive_organizer/adapter/inbound/http/SecurityConfig.java:1)
- [`src/main/java/com/fde/google_drive_organizer/adapter/inbound/http/HomeController.java`](src/main/java/com/fde/google_drive_organizer/adapter/inbound/http/HomeController.java:1)
- [`src/main/resources/templates/index.html`](src/main/resources/templates/index.html:1)
- [`src/main/resources/application.yaml`](src/main/resources/application.yaml:1)

Google OAuth redirect requirements:
- Authorized redirect URI: `http://localhost:8081/login/oauth2/code/google`
- Optional JavaScript origin: `http://localhost:8081`

# Local profile setup (secrets stay untracked)

1. Keep env placeholders in [`src/main/resources/application.yaml`](src/main/resources/application.yaml:1) for `GOOGLE_OAUTH_CLIENT_ID` and `GOOGLE_OAUTH_CLIENT_SECRET`.
2. Put real values in a local profile file [`src/main/resources/application-local.yaml`](src/main/resources/application-local.yaml:1).
3. Add [`src/main/resources/application-local.yaml`](src/main/resources/application-local.yaml:1) to [`.gitignore`](.gitignore:1) so the secrets are never committed.
4. Run the app or tests with `SPRING_PROFILES_ACTIVE=local` (or JVM option `-Dspring.profiles.active=local`).

# IntelliJ run configuration (local profile)

1. Open Run/Debug Configurations for the Spring Boot run config.
2. Set `SPRING_PROFILES_ACTIVE=local` in the Environment variables field.
3. Alternatively, add `-Dspring.profiles.active=local` to VM options if you prefer JVM args.

# Execution Plan

1. Add Spring Security OAuth2 dependencies in [`gradle/libs.versions.toml`](gradle/libs.versions.toml:1) and wire them into [`build.gradle.kts`](build.gradle.kts:1).
2. Configure OAuth2 login in [`SecurityConfig.java`](src/main/java/com/fde/google_drive_organizer/adapter/inbound/http/SecurityConfig.java:1) with static resource access allowed and a post-login success URL of `/`.
3. Update [`HomeController.java`](src/main/java/com/fde/google_drive_organizer/adapter/inbound/http/HomeController.java:1) to map the authenticated user display name into the view model.
4. Update [`index.html`](src/main/resources/templates/index.html:1) to show “not signed in” with a Google sign-in link or “Hello <name>” with logout.
5. Add OAuth client configuration in [`application.yaml`](src/main/resources/application.yaml:1) for the Google registration (client ID/secret, scopes, redirect URI template).
6. Register the redirect URI in Google Cloud Console and set `GOOGLE_OAUTH_CLIENT_ID`/`GOOGLE_OAUTH_CLIENT_SECRET` environment variables before running.
