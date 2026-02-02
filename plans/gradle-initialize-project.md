# Goal

Initialize the Gradle project so dependency versions are centralized in a version catalog and the Java source sets explicitly map to `src/main/java` and `src/test/java`, with JUnit 5 running on the JUnit Platform.

# Context

Project files involved:
- `build.gradle.kts`
- `settings.gradle.kts`
- `gradle/libs.versions.toml`
- `src/main/java`
- `src/test/java`

The build already uses Spring Boot and JUnit Platform dependencies from the version catalog; this setup keeps JUnit 5 enabled while clarifying source set paths and consolidating versions.

# Execution Plan

1. Define all versions in `gradle/libs.versions.toml` (libraries, platforms/BOMs, and plugins).
2. Add plugin aliases in the catalog for build plugins you want versioned centrally.
3. Add library aliases in the catalog for all third-party and internal libraries.
4. Enable the version catalog in `settings.gradle.kts` if your Gradle version requires explicit wiring.
5. Refactor `build.gradle.kts` to use catalog aliases for plugins and dependencies.
6. If a dependency set provides a BOM, add a `platform(...)` dependency (for example, `implementation(platform(libs.spring.boot.bom))`).
7. Add a `sourceSets` block in `build.gradle.kts` that maps `main` to `src/main/java` and `test` to `src/test/java`.
8. Ensure the test task still calls `useJUnitPlatform()` and retains the existing JUnit Platform dependencies.
9. Verify there are no hardcoded dependency versions in `build.gradle.kts` (versions should come from the catalog or BOM).

# Notes

- Spring Boot example: add `spring-boot-dependencies` as a BOM in the catalog and use `platform(libs.spring.boot.bom)` in dependencies.
