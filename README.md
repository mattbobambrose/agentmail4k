[![Maven Central](https://img.shields.io/maven-central/v/com.agentmail4k/agentmail4k)](https://central.sonatype.com/artifact/com.agentmail4k/agentmail4k)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)

# agentmail4k

An idiomatic Kotlin DSL for the [AgentMail](https://agentmail.to) API, built on Ktor and kotlinx.serialization.

## Features

- **DSL-driven API** -- fluent Kotlin builders for every operation
- **Coroutine-native** -- all API calls are `suspend` functions
- **Scoped access** -- work within an inbox or pod context
- **Workflows** -- built-in polling, auto-reply, bulk operations, and webhook handling
- **Type-safe** -- kotlinx.serialization models with full type safety

## Requirements

- JDK 17+
- Kotlin 2.3+

## Installation

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("com.agentmail4k:agentmail4k:0.1.3")
}
```

### Gradle (Groovy)

```groovy
dependencies {
    implementation 'com.agentmail4k:agentmail4k:0.1.3'
}
```

## Quick Start

```kotlin
suspend fun quickStart() {
    val client = AgentMailClient()

    // Create an inbox
    val inbox = client.createInbox("support", "example.com", "Support Team")
    println("Created inbox: ${inbox.email}")

    // Send a message
    val response = client.sendMessage {
        from = inbox.inboxId
        to = listOf("user@example.com")
        subject = "Hello from AgentMail!"
        text = "This is a test message sent with agentmail4k."
    }
    println("Sent message: ${response.messageId}")

    client.close()
}
```

## Configuration

Set your API key via environment variable or pass it directly:

```kotlin
// From environment (reads AGENTMAIL_API_KEY)
val client = AgentMailClient()

// Explicit API key with custom settings
val client = AgentMailClient {
    apiKey = "your-api-key"
    baseUrl = "https://api.agentmail.to"
    timeout {
        connect = 10.seconds
        request = 30.seconds
    }
    retry {
        maxRetries = 3
        retryOnServerErrors = true
    }
}
```

## Project Structure

```
src/main/kotlin/com/agentmail4k/
  sdk/
    AgentMailClient.kt       -- Entry point, holds all API resources
    AgentMailConfig.kt       -- DSL builders for client configuration
    builder/                 -- DSL builders for create/update/list/send operations
    model/                   -- Serializable data classes for API responses
    resource/                -- API resource classes (InboxResource, MessageResource, etc.)
    internal/                -- API path constants and HTTP client factory
  dsl/                       -- High-level extension functions and workflows
```

## Running Tests

```bash
./gradlew test
```

## API Reference

Generated KDoc is available at the [API Reference](https://mattbobambrose.github.io/agentmail4k/kdoc/) and can be built locally:

```bash
./gradlew dokkaGenerate
```

Output is written to `build/dokka/html/`.

## Documentation

Full documentation is available at [mattbobambrose.github.io/agentmail4k](https://mattbobambrose.github.io/agentmail4k/).

## License

[MIT](LICENSE.md)
