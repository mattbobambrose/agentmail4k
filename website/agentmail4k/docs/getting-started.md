# Getting Started

## API Key

The SDK requires an AgentMail API key. You can provide it in two ways:

### Environment Variable (recommended)

Set the `AGENTMAIL_API_KEY` environment variable:

```bash
export AGENTMAIL_API_KEY="your-api-key"
```

Then create a client with no arguments:

```kotlin
--8<-- "GettingStarted.kt:create-client-env"
```

### Explicit API Key

Pass the API key directly in the configuration block:

```kotlin
--8<-- "GettingStarted.kt:create-client-explicit"
```

## Send Your First Message

Create an inbox and send a message:

```kotlin
--8<-- "GettingStarted.kt:quick-start"
```

## Resource Cleanup

`AgentMailClient` implements `Closeable`. Use Kotlin's `use` extension to ensure the client is closed automatically:

```kotlin
--8<-- "GettingStarted.kt:use-closeable"
```

## Next Steps

- [Configuration](configuration.md) — customize timeouts, retries, and more
- [Inboxes](inboxes.md) — create and manage inboxes
- [Messages](messages.md) — send, reply, and forward messages
