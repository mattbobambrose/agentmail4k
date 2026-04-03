# Auto-Reply

Set up automatic replies with pattern-matching rules:

```kotlin
--8<-- "AutoReply.kt:auto-reply"
```

Rules are evaluated in order. The first matching rule handles the message. If no rule matches, the `default` reply is used (if defined).
