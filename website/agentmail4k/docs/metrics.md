# Metrics & Organization

## Query Metrics

Retrieve email metrics for your account:

```kotlin
--8<-- "Metrics.kt:query-metrics"
```

### Metric Periods

Query metrics at different time granularities:

```kotlin
--8<-- "Metrics.kt:metrics-period"
```

| Period | Description |
|--------|-------------|
| `HOUR` | Hourly aggregation |
| `DAY` | Daily aggregation |
| `WEEK` | Weekly aggregation |
| `MONTH` | Monthly aggregation |

## Organization

Retrieve your organization information:

```kotlin
--8<-- "Metrics.kt:get-organization"
```

## Next Steps

- [Copy for Cursor / Claude](llm-cheatsheet.md) — LLM-friendly cheat sheet for AI assistants
