# Getting Started

## Installation

Add the dependency to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.mattbobambrose.agentmail4k:agentmail-sdk:0.1.0")
}
```

The SDK transitively brings in:

| Dependency | Purpose |
|---|---|
| `io.ktor:ktor-client-cio` | Async HTTP engine |
| `io.ktor:ktor-client-content-negotiation` | JSON body handling |
| `io.ktor:ktor-client-auth` | Bearer token auth |
| `kotlinx-serialization-json` | JSON serialization |
| `kotlinx-datetime` | Timestamp handling |
| `kotlinx-coroutines-core` | Coroutine support |

## Creating a Client

```kotlin
import com.mattbobambrose.agentmail4k.sdk.AgentMail

val client = AgentMail {
    apiKey = "your-api-key"
}
```

If you omit `apiKey`, the SDK reads the `AGENTMAIL_API_KEY` environment variable:

```kotlin
val client = AgentMail() // uses AGENTMAIL_API_KEY
```

## Closing the Client

`AgentMail` implements `Closeable`. Always close it when done to release the underlying HTTP connection pool:

```kotlin
client.close()
```

Or use Kotlin's `use` extension:

```kotlin
AgentMail { apiKey = "..." }.use { client ->
    val inbox = client.inboxes.create { username = "test" }
    println(inbox.email)
}
```

## Your First API Call

```kotlin
import com.mattbobambrose.agentmail4k.sdk.AgentMail

suspend fun main() {
    AgentMail().use { client ->
        // List all inboxes
        val result = client.inboxes.list()
        println("You have ${result.count} inboxes")

        for (inbox in result.inboxes) {
            println("  ${inbox.email} (${inbox.inboxId})")
        }
    }
}
```

## Next Steps

- [Configuration](configuration.md) — timeouts, retries, base URL
- [Resources](resources/inboxes.md) — CRUD operations on inboxes, messages, threads, and more
- [Scoped Access](scoped-access.md) — navigate nested resources with `inboxes("id")` and `pods("id")`
- [Workflows](workflows/monitor.md) — high-level utilities for monitoring, auto-reply, and bulk operations
