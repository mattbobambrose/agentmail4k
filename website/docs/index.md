# AgentMail Kotlin SDK

A Kotlin SDK for the [AgentMail API](https://agentmail.to) with an idiomatic DSL-based interface, built on Ktor and kotlinx.serialization.

## Features

- **Type-safe DSL builders** for all API operations
- **Coroutine-native** — every API call is a `suspend` function
- **Scoped access** — navigate resources via `inboxes("id")` and `pods("id")` scopes
- **Workflow utilities** — monitor inboxes, auto-reply, bulk send, poll, and handle webhooks
- **Automatic retry** with exponential backoff on server errors
- **Configurable timeouts** for connect, request, and socket

## Quick Example

```kotlin
val client = AgentMail {
    apiKey = "your-api-key"
}

// Create an inbox
val inbox = client.inboxes.create {
    username = "support"
    domain = "example.com"
    displayName = "Support Team"
}

// Send a message from that inbox
val response = client.inboxes(inbox.inboxId).messages.send {
    to = listOf("user@example.com")
    subject = "Welcome!"
    text = "Thanks for signing up."
}

// Monitor for new messages
client.monitor(inbox.inboxId) {
    pollInterval = 5.seconds
    onMessage { message ->
        println("New message from ${message.from}: ${message.subject}")
    }
}

client.close()
```

## Requirements

- Kotlin 2.1+
- JDK 17+

## License

MIT
