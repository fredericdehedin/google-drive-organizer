# Goal

List Google Drive file names from a configured folder ID and render them on the home page.

# Context

Relevant project files:
- `build.gradle.kts`
- `gradle/libs.versions.toml`
- `src/main/resources/application.yaml`
- `src/main/resources/application-local.yaml`
- `src/main/java/com/fde/google_drive_organizer/adapter/inbound/http/HomeController.java`
- `src/main/resources/templates/index.html`

Key constraints:
- Folder ID configured via `drive.folder-id` placeholder in `application.yaml` with real value in `application-local.yaml`.
- Use Google Drive API v3 via official Java client library.
- Use OAuth2 access token from Spring Security.
- Render file names in the existing index page using HTMX/Thymeleaf.
- Follow Clean Architecture / Hexagonal Architecture principles.

# Execution Plan

1. Add `drive.folder-id` placeholder in `application.yaml` and add real value in `application-local.yaml`.
2. Add Google Drive API v3 Java client dependency in the Gradle version catalog and `build.gradle.kts`.
3. Implement a Drive service that builds a Drive client with the OAuth2 access token and lists files in the configured folder (name only).
4. Update `HomeController` to call the Drive service for authenticated users and pass file names into the view model.
5. Update `index.html` to render the file list in the preview section (HTMX/Thymeleaf).
6. Ensure OAuth scopes include drive.readonly and document any new configuration requirements if needed.

# Implementation Details

## Architecture

The implementation follows Clean Architecture / Hexagonal Architecture with clear separation of concerns:

### Domain Layer
- **`DriveFile`** (`domain/model/DriveFile.java`) - Domain model representing a Google Drive file with id and name
- **`DrivePort`** (`domain/port/outbound/DrivePort.java`) - Port interface for Drive operations
  - Method: `List<DriveFile> listFilesInFolder(String folderId)`
- **`AccessTokenProvider`** (`domain/port/outbound/AccessTokenProvider.java`) - Port interface for OAuth2 token retrieval
  - Method: `String getAccessToken()`

### Application Layer
- **`ListDriveFilesUseCase`** (`application/usecase/ListDriveFilesUseCase.java`) - Use case for listing files
  - Depends on `DrivePort` interface
  - Configured with `drive.folder-id` via `@Value` injection
  - Method: `List<DriveFile> execute()`

### Adapter Layer

#### Inbound Adapters (HTTP)
- **`HomeController`** (`adapter/inbound/http/HomeController.java`) - Web controller
  - Depends on `ListDriveFilesUseCase`
  - Retrieves authenticated user via `@AuthenticationPrincipal OAuth2User`
  - Calls use case and adds files to model
  - Handles `IllegalStateException` when token is unavailable

#### Outbound Adapters
- **`GoogleDriveAdapter`** (`adapter/outbound/drive/GoogleDriveAdapter.java`) - Implements `DrivePort`
  - Depends on `AccessTokenProvider` for token retrieval
  - Builds Google Drive API client with OAuth2 credentials
  - Lists files from specified folder using Drive API v3
  - Throws `IllegalStateException` if access token is not available

- **`GoogleOAuth2AccessTokenProvider`** (`adapter/outbound/oauth/GoogleOAuth2AccessTokenProvider.java`) - Implements `AccessTokenProvider`
  - Depends on Spring Security's `OAuth2AuthorizedClientService`
  - Retrieves OAuth2 access token from `SecurityContextHolder`
  - Returns token value or null if not authenticated

## Key Design Decisions

1. **Token Retrieval Abstraction**: OAuth2 token retrieval is abstracted behind the `AccessTokenProvider` port interface, eliminating parameter drilling through the call chain and making the domain layer independent of Spring Security implementation details.

2. **Dependency Direction**: All dependencies point inward toward the domain layer. The domain layer has no knowledge of Spring Security, Google Drive API, or web frameworks.

3. **Error Handling**: When no access token is available, `GoogleDriveAdapter` throws `IllegalStateException`, which is caught by `HomeController` to gracefully handle the error by displaying an empty file list.

4. **Configuration**: The folder ID is injected into the use case via Spring's `@Value` annotation, keeping configuration concerns at the application layer boundary.

5. **Testing**: Test configuration provides a dummy `drive.folder-id` value via `@TestPropertySource` to allow context loading without requiring actual Google Drive credentials.

## Files Created/Modified

### Created
- `src/main/java/com/fde/google_drive_organizer/domain/model/DriveFile.java`
- `src/main/java/com/fde/google_drive_organizer/domain/port/outbound/DrivePort.java`
- `src/main/java/com/fde/google_drive_organizer/domain/port/outbound/AccessTokenProvider.java`
- `src/main/java/com/fde/google_drive_organizer/application/usecase/ListDriveFilesUseCase.java`
- `src/main/java/com/fde/google_drive_organizer/adapter/outbound/drive/GoogleDriveAdapter.java`
- `src/main/java/com/fde/google_drive_organizer/adapter/outbound/oauth/GoogleOAuth2AccessTokenProvider.java`

### Modified
- `gradle/libs.versions.toml` - Added Google Drive API dependencies
- `build.gradle.kts` - Added Google Drive API client library
- `src/main/resources/application.yaml` - Added `drive.folder-id` placeholder
- `src/main/java/com/fde/google_drive_organizer/adapter/inbound/http/HomeController.java` - Integrated use case
- `src/main/resources/templates/index.html` - Added file list rendering
- `src/test/java/com/fde/google_drive_organizer/GoogleDriveOrganizerApplicationTests.java` - Added test configuration
