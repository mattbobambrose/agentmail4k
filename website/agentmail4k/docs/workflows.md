# Workflows

The SDK includes high-level workflow utilities built on top of the core API.

## Monitor

Monitor an inbox for new messages by polling at a configurable interval. Returns a coroutine `Job` that runs in the background.

```kotlin
--8<-- "Workflows.kt:monitor"
```

### Full Message Content

Use `onFullMessage` instead of `onMessage` to receive complete message bodies:

```kotlin
--8<-- "Workflows.kt:monitor-full"
```

## Poll

`poll()` is a simplified wrapper around `monitor()` for the common case of just handling messages:

```kotlin
--8<-- "Workflows.kt:poll"
```

## Auto-Reply

Set up automatic replies with pattern-matching rules:

```kotlin
--8<-- "Workflows.kt:auto-reply"
```

Rules are evaluated in order. The first matching rule handles the message. If no rule matches, the `default` reply is used (if defined).

## Bulk Operations

### Send to Multiple Recipients

```kotlin
--8<-- "Workflows.kt:bulk-send"
```

### Process All Threads

Iterate over all threads in an inbox with automatic pagination:

```kotlin
--8<-- "Workflows.kt:bulk-threads"
```
