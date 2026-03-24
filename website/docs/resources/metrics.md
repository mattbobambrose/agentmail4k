# Metrics

Query email event metrics aggregated by time period.

## Query Metrics

```kotlin
val result = client.inboxes("inbox_abc").metrics.query {
    period = MetricsPeriod.DAY
    before = "2024-12-31T23:59:59Z"
    after = "2024-01-01T00:00:00Z"
}

for (metric in result.metrics) {
    println("${metric.eventType}: ${metric.count} at ${metric.timestamp}")
}
```

**Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `period` | `MetricsPeriod?` | Aggregation period |
| `before` | `String?` | Metrics before this timestamp |
| `after` | `String?` | Metrics after this timestamp |

## Periods

| Period | Description |
|---|---|
| `MetricsPeriod.HOUR` | Hourly aggregation |
| `MetricsPeriod.DAY` | Daily aggregation |
| `MetricsPeriod.WEEK` | Weekly aggregation |
| `MetricsPeriod.MONTH` | Monthly aggregation |

## Scoped Access

Metrics are available at multiple levels:

```kotlin
// Organization level
client.metrics.query { period = MetricsPeriod.WEEK }

// Inbox level
client.inboxes("inbox_abc").metrics.query { period = MetricsPeriod.DAY }

// Pod level
client.pods("pod_123").metrics.query { period = MetricsPeriod.MONTH }
```
