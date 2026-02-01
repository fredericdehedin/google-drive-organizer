# Goal

Document the changes to the Gradle build so the `main` and `test` source sets are explicitly mapped to `src/main/java` and `src/test/java`, with JUnit 5 tests running on the JUnit Platform.

# Context

Project files involved:
- `build.gradle.kts`
- `src/main/java`
- `src/test/java`

The build already uses Spring Boot and JUnit Platform dependencies from the version catalog; this update makes the source set paths explicit while keeping JUnit 5 enabled.

# Execution Plan

1. Locate the Gradle build configuration in `build.gradle.kts`.
2. Add a `sourceSets` block that sets `main` to `src/main/java` and `test` to `src/test/java`.
3. Ensure the test task still calls `useJUnitPlatform()` and retains the existing JUnit Platform dependencies.
4. Save the documentation for this change in `docs/ai/gradle-source-sets-junit.md`.
