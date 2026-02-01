# Goal

Set up a Gradle version catalog using `gradle/libs.versions.toml` so all dependency and plugin versions are centralized, with Spring Boot using its BOM coordinates for alignment.

# Context

Project files involved:
- `build.gradle.kts`
- `settings.gradle.kts`
- `gradle/libs.versions.toml`

# Execution Plan

1. Define all versions in `gradle/libs.versions.toml` (libraries, platforms/BOMs, and plugins).
2. Add plugin aliases in the catalog for build plugins you want versioned centrally.
3. Add library aliases in the catalog for all third-party and internal libraries.
4. Enable the version catalog in `settings.gradle.kts` if your Gradle version requires explicit wiring.
5. Refactor `build.gradle.kts` to use the catalog aliases for plugins and dependencies.
6. If a dependency set provides a BOM, add a `platform(...)` dependency (e.g., `implementation(platform(libs.spring.boot.bom))`).
7. Verify there are no hardcoded dependency versions in `build.gradle.kts` (versions should come from the catalog or BOM).

Notes:
- Spring Boot example: add `spring-boot-dependencies` as a BOM in the catalog and use `platform(libs.spring.boot.bom)` in dependencies.
