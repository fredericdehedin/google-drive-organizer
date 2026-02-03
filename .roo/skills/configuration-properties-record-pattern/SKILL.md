---
name: configuration-properties-record-pattern
description: Create Spring Boot configuration properties using immutable records, prefix-based binding, optional default values, and lightweight tests without @SpringBootTest.
---

# Configuration Properties & Test Pattern

This skill enforces the following conventions when creating configuration properties and their tests in the project.

---

## 1️. Configuration Properties Class

- Use **records** for immutable configuration properties.
- Use **`@ConfigurationProperties(prefix = "...")`** for binding.
- Use **`@DefaultValue("...")`** to provide defaults.
- Do **not** use `@Configuration` on the record if `@ConfigurationPropertiesScan` is enabled.
- Keep naming consistent with your project convention.

### Example:

```java
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "thumbnail.cache")
public record ThumbnailCacheConfig(
        @DefaultValue("/tmp/cache") String directory
) {}
```
Property binding:

thumbnail.cache.directory → directory field

Default value is applied if property is missing

## 2. Testing Configuration Properties
Do not use @SpringBootTest

+ Use @ExtendWith(SpringExtension.class)
+ Use @ContextConfiguration(classes = TestConfig.class)
+ Use @EnableConfigurationProperties(...) for the config class
+ Use @TestPropertySource(properties = {...}) to inject test values

Autowire the configuration properties record in the test

Example Test:
```java
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ThumbnailCacheConfigTest.TestConfig.class)
@TestPropertySource(properties = {
        "thumbnail.cache.directory=/tmp/test-cache"
})
class ThumbnailCacheConfigTest {

    @EnableConfigurationProperties(ThumbnailCacheConfig.class)
    static class TestConfig {}

    @Autowired
    private ThumbnailCacheConfig config;

    @Test
    void shouldBindDirectoryProperty() {
        assertThat(config.directory()).isEqualTo("/tmp/test-cache");
    }
}
```
+ Lightweight and isolated test
+ Works in multi-module Gradle setups
+ Verifies property binding and defaults

## 3. Usage in code

Once the configuration properties record is defined and scanned via `@ConfigurationPropertiesScan`:

- Autowire the record wherever you need the values:

```java
@Service
public class ThumbnailService {

    private final ThumbnailCacheConfig config;

    public ThumbnailService(ThumbnailCacheConfig config) {
        this.config = config;
    }

    public Path getCacheDirectory() {
        return Path.of(config.directory());
    }
}
```

## 4. Conventions & Best Practices
+ Keep property keys consistent with prefix hierarchy
+ Avoid @SpringBootTest in property tests → tests stay fast and isolated
+ Favor primitive types like `boolean`, `int` etc. instead of boxed types
+ For booleans: use `@DefaultValue` if `true` needs to be enforced (e.g. for feature flags)
+ For multi-module projects, ensure the module with the record is a dependency of the module containing the test
+ For multiple feature flags, repeat the same pattern for each record + test pair

## 4️⃣ Anti-Patterns (Do NOT)
+ Mutable classes with @Data or @Setter for feature flags
+ @SpringBootTest in simple property binding tests
+ Hard-coded property values in production code instead of config binding
+ Skipping @TestPropertySource or @DefaultValue → may cause NPEs in tests

## Goal
+ Enforce project-wide immutable configuration properties
+ Ensure defaults are applied safely
+ Provide fast, deterministic tests for properties
+ Reduce boilerplate and mistakes in multi-module setups