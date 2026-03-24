# Configuration

The `AgentMail` client is configured via a DSL builder. All settings have sensible defaults.

## Full Configuration Example

```kotlin
val client = AgentMail {
    apiKey = "your-api-key"
    baseUrl = "https://api.agentmail.to"

    timeout {
        connect = 10.seconds
        request = 30.seconds
        socket = 30.seconds
    }

    retry {
        maxRetries = 3
        retryOnServerErrors = true
    }
}
```

## API Key

The API key is resolved in this order:

1. Explicit value via `apiKey = "..."`
2. `AGENTMAIL_API_KEY` environment variable

If neither is set, the client throws an `IllegalStateException` at construction time.

```kotlin
// Explicit
val client = AgentMail { apiKey = "am_live_..." }

// From environment
val client = AgentMail()
```

## Base URL

Override the API base URL (useful for testing or self-hosted instances):

```kotlin
val client = AgentMail {
    baseUrl = "http://localhost:8080"
}
```

Default: `https://api.agentmail.to`

## Timeouts

Configure HTTP timeout durations using Kotlin's `Duration` type:

```kotlin
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Duration.Companion.minutes

val client = AgentMail {
    timeout {
        connect = 5.seconds   // TCP connection timeout (default: 10s)
        request = 1.minutes   // Total request timeout (default: 30s)
        socket = 30.seconds   // Socket read/write timeout (default: 30s)
    }
}
```

## Retry

Configure automatic retry behavior for server errors (5xx):

```kotlin
val client = AgentMail {
    retry {
        maxRetries = 5              // default: 3
        retryOnServerErrors = true  // default: true
    }
}
```

Retries use exponential backoff automatically.

## Configuration Data Classes

The resolved configuration is represented by these data classes:

| Class | Fields |
|---|---|
| `AgentMailConfig` | `apiKey`, `baseUrl`, `timeout`, `retry` |
| `TimeoutConfig` | `connect`, `request`, `socket` |
| `RetryConfig` | `maxRetries`, `retryOnServerErrors` |
