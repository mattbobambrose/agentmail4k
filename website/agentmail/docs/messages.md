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

```kotlin
--8<-- "Messages.kt:update-message"
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
