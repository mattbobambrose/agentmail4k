# Auto-Reply

The `autoReply` extension function monitors an inbox and automatically replies to incoming messages based on configurable rules.

## Basic Usage

```kotlin
import to.agentmail.sdk.workflow.autoReply
import kotlin.time.Duration.Companion.seconds

val job = client.autoReply("inbox_abc") {
    pollInterval = 5.seconds

    // Rule-based replies
    rule(
        match = { msg -> msg.subject?.contains("urgent", ignoreCase = true) == true },
        reply = { msg ->
            text = "We received your urgent message and will respond within 1 hour."
            html = "<p>We received your urgent message and will respond within <b>1 hour</b>.</p>"
        }
    )

    rule(
        match = { msg -> msg.from.endsWith("@partner.com") },
        reply = { msg ->
            text = "Hi! Your message has been forwarded to the partnership team."
        }
    )

    // Default reply for unmatched messages
    default { msg ->
        text = "Thanks for reaching out! We'll get back to you soon."
    }
}

// Stop auto-replying
job.cancel()
```

## How Rules Work

1. For each incoming message, rules are evaluated **in order**
2. The **first matching rule** sends its reply
3. If no rule matches, the **default** reply is used (if set)
4. If no rule matches and no default is set, the message is ignored

## Rule Parameters

The `rule` function takes:

| Parameter | Type | Description |
|---|---|---|
| `match` | `(Message) -> Boolean` | Predicate to test the message |
| `reply` | `ReplyBuilder.(Message) -> Unit` | Reply builder with access to the original message |

The reply builder has access to `text` and `html` fields — the same as `messages.reply()`.

## Built on Monitor

`autoReply` is built on top of the [Monitor](monitor.md) workflow. It inherits the same polling behavior, coroutine scope management, and error handling.
