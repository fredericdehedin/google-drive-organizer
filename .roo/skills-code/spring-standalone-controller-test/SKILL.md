---
name: spring-standalone-controller-test
description: Write Spring MVC controller tests using standaloneSetup, manual mocks, no Spring context, and MockMvc.
---

# Spring Standalone Controller Test Convention

When writing or modifying controller tests in this project, follow this pattern strictly.

## Core Rule

Controller tests MUST:

- NOT use @SpringBootTest
- NOT use @WebMvcTest
- NOT load any Spring context
- NOT use @Autowired
- NOT use @MockBean

Tests must be pure unit-style tests using `MockMvcBuilders.standaloneSetup`.

---

## Required Structure

### 1. Mock dependencies manually

Use Mockito:

```java
private final SomeUseCase someUseCase = mock(SomeUseCase.class);
```

### 2. Instantiate controller manually
```java
private final MockMvc mockMvc = MockMvcBuilders
        .standaloneSetup(new SomeController(someUseCase))
        .build();
```

Dependencies must be passed via constructor

No field injection

3. Authentication

If endpoint requires authentication:

Use @WithMockUser

Do NOT configure full security context

Do NOT load security configuration

4. Test Structure

Each endpoint must have:

Success case

Not-found case (if applicable)

Error case (if applicable)

Example pattern:

@Test
@WithMockUser
void shouldReturnResourceWhenFoundAndAuthenticated() throws Exception {
    when(useCase.execute(id)).thenReturn(Optional.of(result));

    mockMvc.perform(get("/api/resource/{id}", id))
            .andExpect(status().isOk())
            .andExpect(content().contentType(...))
            .andExpect(content().bytes(...));
}

Assertions

Tests must verify:

HTTP status

Content type (if applicable)

Response body

Relevant headers (if applicable)

Avoid minimal assertions like only checking status.

Anti-Patterns (Forbidden)

@SpringBootTest

@WebMvcTest

Loading application context

Using real beans

Using @MockBean

Injecting via @Autowired

Mixing integration-style testing with unit-style controller tests

Goal

Controller tests must:

Be fast

Be isolated

Mock all dependencies

Test only HTTP contract and controller behavior
