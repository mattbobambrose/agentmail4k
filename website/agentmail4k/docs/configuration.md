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

## Rate Limiting

Configure client-side rate limiting for outgoing messages. Limits are enforced per sender or per recipient using a sliding time window.

```kotlin
--8<-- "Configuration.kt:rate-limit-config"
```

| Parameter | Default | Description |
|-----------|---------|-------------|
| `maxMessages` | 1 | Max messages allowed within the window |
| `window` | 5 seconds | Sliding time window duration |
| `onLimitExceeded` | `STOP` | Action when limit is exceeded |

### Actions

| Action | Behavior |
|--------|----------|
| `STOP` | Throws `RateLimitExceededException` |
| `SKIP` | Logs a warning and returns `null` without sending |
| `DELAY` | Suspends until the window clears, then sends |

When using `SKIP`, send functions return `SendMessageResponse?` — check for `null` to detect skipped messages.

### Delay Example

Use `DELAY` to automatically throttle without dropping or failing:

```kotlin
--8<-- "Configuration.kt:rate-limit-delay"
```

## Next Steps

- [Inboxes](inboxes.md) — create and manage inboxes
- [Messages](messages.md) — send, reply, and forward messages
- [Threads](threads.md) — list and manage conversation threads
