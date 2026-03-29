---
name: Parameterized Test Pattern
description: Minimal rules for project-standard parameterized tests.
---

# Parameterized Test Pattern

Use when converting simple domain tests to the standard parameterized structure.
Reference: [`PositionOutOfSyncTest.java`](portfolio_and_positions/domain/src/test/java/com/lgt/fwb/portfolios/domain/client_portfolio_position/position_list/position/out_of_sync/PositionOutOfSyncTest.java).

## Required structure (order)
1) `dataProvider()`
2) `TestData` static inner class
3) `testVariations(TestData testData)`
Non-parameterized tests keep their relative order.

## Parameterized test wiring
- `@ParameterizedTest(name = "<testName> {0}")`
- `@MethodSource("dataProvider")`
- `TestData#toString()` for readable names.

## Data provider
- `Stream.of(arguments(TestData.create()...))`
- `given...` then `expected...` per row.
- Order cases: null, empty, single, many (or group by expected result, keeping that order inside groups).

## TestData
- `create()` factory.
- Fluent `given...` / `expected...` setters.
- Fields match setter names.

## Test body
- Use `testData` fields.
- Assert with AssertJ.

## Imports
- `@ParameterizedTest`, `@MethodSource`, static `Stream.of`, `Arguments.arguments`.
