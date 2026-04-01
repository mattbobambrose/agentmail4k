# AgentMail Kotlin SDK

An idiomatic Kotlin SDK for the [AgentMail](https://agentmail.to) API, built on Ktor and kotlinx.serialization.

## Features

- **DSL-driven API** — fluent Kotlin builders for every operation
- **Coroutine-native** — all API calls are `suspend` functions
- **Scoped access** — work within an inbox or pod context
- **Workflows** — built-in polling, auto-reply, bulk operations, and webhook handling
- **Type-safe** — kotlinx.serialization models with full type safety

## Installation

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("com.agentmail4k:agentmail-sdk:0.1.0")
}
```

### Gradle (Groovy)

```groovy
dependencies {
    implementation 'com.agentmail4k:agentmail-sdk:0.1.0'
}
```

## Quick Start

```kotlin
--8<-- "GettingStarted.kt:quick-start"
```

## Next Steps

- [Getting Started](getting-started.md) — set up your API key and send your first message
- [Configuration](configuration.md) — customize timeouts, retries, and base URL
- [Workflows](workflows.md) — polling, auto-reply, and bulk operations
