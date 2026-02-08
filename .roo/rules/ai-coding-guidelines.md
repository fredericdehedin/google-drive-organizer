# AI Coding Guidelines (Global)

These rules apply to ALL AI-assisted code:

- Prefer readability over cleverness
- Follow Clean Architecture boundaries
  - "adapters/inbound/*" and "adapters/outbound/*" contains implementations
  - use "domain" contains domain classes, follow ddd, contains logic to avoid anemic domain model
  - use "application" contains use cases (UC suffix) and interfaces for ports
- Do not introduce new dependencies without justification
- Prefer immutable data structures
- Avoid unnecessary abstractions
- Follow YAGNI - only add functionality when there's an actual use case
- Do not add Javadoc comments unless they explain WHY something is done a certain way
- Do not use Optional unless mentioned explicitly

# Testing Guidelines

When writing tests:

- Use JUnit 5
- Use AssertJ for assertions
- Use Mockito for mocking
- leverage TestFixtures of "a" class and it's builders
- Prefer explicit mocking over ArgumentCaptor (avoid using org.mockito.ArgumentMatchers eq, etc.)
- Avoid lenient mocks
- Prefer parameterized tests where applicable
- Test names must start with "should..."
- Avoid testing implementation details
