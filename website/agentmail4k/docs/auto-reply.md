# Auto-Reply

Set up automatic replies with pattern-matching rules:

```kotlin
--8<-- "AutoReply.kt:auto-reply"
```

Rules are evaluated in order. The first matching rule handles the message. If no rule matches, the `default` reply is used (if defined).

## Next Steps

- [Bulk Operations](bulk-operations.md) — send to multiple inboxes at once
- [Webhooks](webhooks.md) — receive and verify webhook events
- [Pods](pods.md) — group inboxes into isolated pods
