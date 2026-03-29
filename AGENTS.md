# Skills

Reusable task instructions are available in [.agents/skills/](./.agents/skills/). Check this folder for relevant skills before starting a task.

# AI Coding Guidelines (Global)

These rules apply to ALL AI-assisted code:
- Do not add Javadoc comments unless they explain WHY something is done a certain way
- Do not use Optional unless mentioned explicitly
- Follow the Null Object pattern. Prefer dedicated Null* implementations instead of returning or passing null.
  - Value objects wrapping primitives or Java types (int, String, BigDecimal, LocalDate, etc.) must never be null.
  - Always instantiate the wrapper/value object; the wrapped inner value may be null if it represents "no value".

# Testing Guidelines

When writing tests:

- Use AssertJ for assertions
- Use Mockito for mocking
- leverage TestFixtures of "a" class and it's builders
- Prefer explicit mocking over ArgumentCaptor (avoid using org.mockito.ArgumentMatchers eq, etc.)
- Avoid generic argument matchers (anyString(), anyInt(), any(), etc.) - use concrete values instead to make tests more explicit and catch regressions
- Avoid lenient mocks
- Prefer parameterized tests where applicable (see skill .agents/skills/test/parameterized-test-pattern/SKILL.md)
- Test names must start with "should..."

# Project specific info

**Dependency version catalog:** `gradle/libs.versions.toml`

## Commands

```bash
# Build
./gradlew build --parallel

# Test (all)
./gradlew test
```
