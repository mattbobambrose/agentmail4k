# Monitoring

Monitor an inbox for new messages by polling at a configurable interval. Returns a coroutine `Job` that runs in the background.

```kotlin
--8<-- "Monitoring.kt:monitor"
```

## Full Message Content

Use `onFullMessage` instead of `onMessage` to receive complete message bodies:

```kotlin
--8<-- "Monitoring.kt:monitor-full"
```

## Filtering Messages

Use `filterBy` to only process messages that match a predicate. The filter has access to all `Message` attributes — labels, sender, subject, attachments, etc.

```kotlin
--8<-- "Monitoring.kt:monitor-filter"
```

You can combine multiple conditions in a single filter:

```kotlin
--8<-- "Monitoring.kt:monitor-filter-advanced"
```

## Poll

`poll()` is a simplified wrapper around `monitor()` for the common case of just handling messages:

```kotlin
--8<-- "Monitoring.kt:poll"
```

`poll()` also accepts a `filter` parameter:

```kotlin
--8<-- "Monitoring.kt:poll-filter"
```

## Next Steps

- [Auto-Reply](auto-reply.md) — set up rule-based automatic replies
- [Bulk Operations](bulk-operations.md) — send to multiple inboxes at once
- [Webhooks](webhooks.md) — receive and verify webhook events
