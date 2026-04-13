# Rate Limiting

Client-side rate limiting protects against sending too many messages from a single inbox or to a single recipient. Limits use a sliding time window and are enforced on all send operations — `sendMessage`, `replyToMessage`, `replyAllToMessage`, and `forwardMessage`.

## Per-Sender Limiting

Tracks messages by sender inbox ID. Each inbox has its own independent counter:

```kotlin
--8<-- "RateLimiting.kt:per-sender"
```

## Per-Recipient Limiting

Tracks messages by recipient email address. Each address in `to`, `cc`, and `bcc` is checked independently:

```kotlin
--8<-- "RateLimiting.kt:per-recipient"
```

## Actions

When a limit is exceeded, the configured action determines what happens:

| Action | Behavior |
|--------|----------|
| `STOP` | Throws `RateLimitExceededException` (default) |
| `SKIP` | Returns `null` without sending |
| `DELAY` | Suspends until the window clears, then sends |

### DELAY — Automatic Throttling

Use `DELAY` to automatically pace outgoing messages without dropping or failing:

```kotlin
--8<-- "RateLimiting.kt:delay"
```

### STOP — Fail Fast

Use `STOP` to immediately halt when a limit is hit. Catch `RateLimitExceededException` to handle it:

```kotlin
--8<-- "RateLimiting.kt:stop"
```

### SKIP — Silent Drop

Use `SKIP` when it's acceptable to silently drop excess messages. Check for a `null` return to detect skipped sends — see the [per-recipient example](#per-recipient-limiting) above.

## Combining Both Limits

Per-sender and per-recipient limits can be used together. The sender limit is checked first, then each recipient:

```kotlin
--8<-- "RateLimiting.kt:combined"
```

## Replies and Forwards

Rate limiting applies to all send operations, not just `sendMessage`:

```kotlin
--8<-- "RateLimiting.kt:reply-rate-limited"
```

## Next Steps

- [Configuration](configuration.md) — full client configuration reference
- [Messages](messages.md) — send, reply, and forward messages
- [Monitoring](monitoring.md) — poll for new messages in real time
