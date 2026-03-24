# Messages

Messages are individual emails within a thread. Access messages through an inbox scope.

## List Messages

```kotlin
val result = client.inboxes("inbox_abc").messages.list {
    limit = 50
    ascending = false
    labels = listOf("inbox")
    includeSpam = false
    includeBlocked = false
    includeTrash = false
    before = "2024-01-01T00:00:00Z"
    after = "2023-01-01T00:00:00Z"
}

for (msg in result.messages) {
    println("${msg.from} → ${msg.subject}")
}
```

**Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `limit` | `Int?` | Max results per page |
| `pageToken` | `String?` | Pagination cursor |
| `labels` | `List<String>?` | Filter by labels |
| `before` | `String?` | Messages before this timestamp |
| `after` | `String?` | Messages after this timestamp |
| `ascending` | `Boolean?` | Sort order |
| `includeSpam` | `Boolean?` | Include spam messages |
| `includeBlocked` | `Boolean?` | Include blocked messages |
| `includeTrash` | `Boolean?` | Include trashed messages |

## Get Message

```kotlin
val message = client.inboxes("inbox_abc").messages.get("msg_123")
println(message.text)
println(message.html)
```

## Send Message

```kotlin
val response = client.inboxes("inbox_abc").messages.send {
    to = listOf("recipient@example.com")
    cc = listOf("cc@example.com")
    bcc = listOf("bcc@example.com")
    subject = "Hello"
    text = "Plain text body"
    html = "<h1>HTML body</h1>"
}

println(response.messageId)
println(response.threadId)
```

At least one recipient in `to` is required.

## Reply

Reply to the sender of a message:

```kotlin
val response = client.inboxes("inbox_abc").messages.reply("msg_123") {
    text = "Thanks for your message!"
    html = "<p>Thanks for your message!</p>"
}
```

## Reply All

Reply to all recipients:

```kotlin
val response = client.inboxes("inbox_abc").messages.replyAll("msg_123") {
    text = "Replying to everyone"
}
```

## Forward

Forward a message to new recipients:

```kotlin
val response = client.inboxes("inbox_abc").messages.forward("msg_123") {
    to = listOf("forward-to@example.com")
    subject = "FYI: forwarded message"
    text = "See below."
}
```

## Update Message

Update message labels:

```kotlin
val updated = client.inboxes("inbox_abc").messages.update("msg_123") {
    labels = listOf("important", "reviewed")
}
```

## Get Attachment

Download an attachment as raw bytes:

```kotlin
val attachment = client.inboxes("inbox_abc").messages.getAttachment("msg_123", "att_456")
println(attachment.contentType)  // e.g. "application/pdf"
val bytes: ByteArray = attachment.data
```

## Get Raw Message

Get the original RFC 2822 message:

```kotlin
val raw = client.inboxes("inbox_abc").messages.getRaw("msg_123")
println(raw.raw)  // full MIME content
```
