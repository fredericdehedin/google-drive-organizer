# AI Coding Guidelines (Global)
+
+These rules apply to ALL AI-assisted code:
+
+- Prefer readability over cleverness
+- Follow Clean Architecture boundaries
+- Do not introduce new dependencies without justification
+- Prefer immutable data structures
+- Avoid unnecessary abstractions
+
+# Testing Guidelines
+
+When writing tests:
+
+- Use JUnit 5
+- Use AssertJ for assertions
+- Use Mockito for mocking
+- leverage TestFixtures of "a" class and it's builders
+- Prefer explicit mocking over ArgumentCaptor (avoid using org.mockito.ArgumentMatchers eq, etc.)
+- Avoid lenient mocks
+- Prefer parameterized tests where applicable
+- Test names must start with "should..."
+- Avoid testing implementation details
