# Messages

Messages are emails sent and received through AgentMail inboxes.

## Send a Message

```kotlin
--8<-- "Messages.kt:send-message"
```

### CC and BCC

```kotlin
--8<-- "Messages.kt:send-with-cc-bcc"
```

## List Messages

```kotlin
--8<-- "Messages.kt:list-messages"
```

### Filter Messages

```kotlin
--8<-- "Messages.kt:filter-messages"
```

## Full Message Content

List operations return message summaries without body content. Use `toFullMessage()` to fetch the complete message:

```kotlin
--8<-- "Messages.kt:full-message"
```

## Reply

```kotlin
--8<-- "Messages.kt:reply"
```

## Reply All

```kotlin
--8<-- "Messages.kt:reply-all"
```

## Forward

```kotlin
--8<-- "Messages.kt:forward"
```

## Update Labels

Replace all labels on a message:

```kotlin
--8<-- "Messages.kt:update-message"
```

### Add / Remove Labels

Incrementally add or remove labels without replacing the full set. This is useful for tracking read/unread state:

```kotlin
--8<-- "Messages.kt:add-remove-labels"
```

## Attachments

```kotlin
--8<-- "Messages.kt:attachment"
```

## Raw Message

Get the raw RFC 822 formatted email:

```kotlin
--8<-- "Messages.kt:raw-message"
```

## Next Steps

- [Threads](threads.md) — list and manage conversation threads
- [Drafts](drafts.md) — create, update, and send drafts
- [Monitoring](monitoring.md) — poll for new messages in real time
