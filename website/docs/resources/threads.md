# Threads

Threads group related messages into conversations. Access threads through an inbox scope or at the top level.

## List Threads

```kotlin
val result = client.inboxes("inbox_abc").threads.list {
    limit = 25
    ascending = false
    labels = listOf("inbox")
    includeSpam = false
    includeBlocked = false
    includeTrash = false
}

for (thread in result.threads) {
    println("${thread.subject} — ${thread.messageCount} messages")
}
```

**Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `limit` | `Int?` | Max results per page |
| `pageToken` | `String?` | Pagination cursor |
| `labels` | `List<String>?` | Filter by labels |
| `before` | `String?` | Threads before this timestamp |
| `after` | `String?` | Threads after this timestamp |
| `ascending` | `Boolean?` | Sort order |
| `includeSpam` | `Boolean?` | Include spam threads |
| `includeBlocked` | `Boolean?` | Include blocked threads |
| `includeTrash` | `Boolean?` | Include trashed threads |

## Get Thread

```kotlin
val thread = client.inboxes("inbox_abc").threads.get("thread_123")
println(thread.subject)
println(thread.senders)
println(thread.messageCount)
```

## Delete Thread

```kotlin
// Soft delete (move to trash)
client.inboxes("inbox_abc").threads.delete("thread_123")

// Permanent delete
client.inboxes("inbox_abc").threads.delete("thread_123") {
    permanent = true
}
```

## Get Thread Attachment

```kotlin
val attachment = client.inboxes("inbox_abc").threads.getAttachment("thread_123", "att_456")
println(attachment.contentType)
val bytes: ByteArray = attachment.data
```

## Top-Level Access

You can also access threads at the organization level (across all inboxes):

```kotlin
val allThreads = client.threads.list { limit = 100 }
```
