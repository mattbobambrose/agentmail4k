# Configuration

The `AgentMailClient` is configured using a DSL builder. All settings have sensible defaults.

## Full Configuration

```kotlin
--8<-- "Configuration.kt:full-config"
```

## Defaults

When no configuration is provided, the following defaults apply:

```kotlin
--8<-- "Configuration.kt:defaults"
```

## Timeouts

Configure connection, request, and socket timeouts using Kotlin's `Duration` type:

```kotlin
--8<-- "Configuration.kt:timeout-config"
```

| Parameter | Default | Description |
|-----------|---------|-------------|
| `connect` | 10 seconds | Time to establish a connection |
| `request` | 30 seconds | Total request timeout |
| `socket` | 30 seconds | Time between data packets |

## Retries

Configure automatic retry behavior for failed requests:

```kotlin
--8<-- "Configuration.kt:retry-config"
```

| Parameter | Default | Description |
|-----------|---------|-------------|
| `maxRetries` | 3 | Number of retry attempts |
| `retryOnServerErrors` | true | Retry on 5xx server errors |
