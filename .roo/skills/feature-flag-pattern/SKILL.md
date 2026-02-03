---
name: feature-flag-pattern
description: >
  Defines a feature flag using the negative `.inactive` convention.
  Default is always true so the feature is inactive if no configuration exists.
  Delegates record creation and test setup to the configuration-properties-record-pattern skill.
depends-on:
  - configuration-properties-record-pattern
---

# Feature Flag Instructions

1. **Feature flag namings**: 
   Always append `Inactive` to the feature name.
       - Example: `clientPersonSearchInactive`
   Use "FeatureFlagConfig" as suffix for ConfigurationProperties class, e.g. `ClientPersonSearchFeatureFlagConfig`.

2. **Default value**: Always `true` (feature inactive if not configured). Set it to true in the code and also in the application.yaml. 
   example application.yaml
```yaml
some-feature:
  client-person-search-inactive: true
  ```

3. **Placement**: Place in the same package as the component using it

4. **Delegate record creation and test setup** to `configuration-properties-record-pattern`.

5. **Usage guidance**:
    - Access the flag via the record accessor:
      ```java
      if (!someConfig.clientPersonSearchInactive()) {
          // feature-specific code
      }
      ```
      
6. Testing guidance:
   - Use `@Nested` test classes to isolate "active" and "inactive" scenarios.
   - **Mock the feature flag within each nested class** instead of overriding properties in YAML:

   Example structure:
      ```java
   @Nested
   class FeatureFlagOfSomeFeatureInactive {
   @BeforeEach
   void setup() {
   when(someFeatureFlagConfig.someFeatureInactive()).thenReturn(true);
   }

       @Test
       void shouldNotExecuteFeature() {
           // test behavior when feature is inactive
       }
   }

   @Nested
   class FeatureFlagOfSomeFeatureActive {
   @BeforeEach
   void setup() {
   when(someFeatureFlagConfig.someFeatureInactive()).thenReturn(false);
   }

       @Test
       void shouldExecuteFeature() {
           // test behavior when feature is active
       }
   }
      ```
   - This ensures each state of the flag is tested independently.

7. **Do not include**:
    - Additional methods (like `isActive()`)
    - `@Data` or mutable setters
    - `@SpringBootTest`

---


