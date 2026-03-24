# Inboxes

Inboxes are the primary email endpoints in AgentMail. Each inbox has a unique email address and can send and receive messages.

## List Inboxes

```kotlin
val result = client.inboxes.list {
    limit = 25
    ascending = true
    pageToken = "..."  // for pagination
}

for (inbox in result.inboxes) {
    println("${inbox.email} — ${inbox.displayName}")
}
```

**Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `limit` | `Int?` | Max results per page |
| `pageToken` | `String?` | Pagination cursor |
| `ascending` | `Boolean?` | Sort order |

**Returns:** `InboxList` with `count`, `inboxes`, `limit`, and `nextPageToken`.

## Create Inbox

```kotlin
val inbox = client.inboxes.create {
    username = "support"
    domain = "example.com"
    displayName = "Support Team"
    clientId = "client-123"
}

println(inbox.email)    // support@example.com
println(inbox.inboxId)  // unique ID
```

**Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `username` | `String?` | Local part of the email address |
| `domain` | `String?` | Domain for the email address |
| `displayName` | `String?` | Human-readable name |
| `clientId` | `String?` | Optional client identifier |

All parameters are optional — the API assigns defaults if omitted.

## Get Inbox

```kotlin
val inbox = client.inboxes.get("inbox_abc123")
```

## Update Inbox

```kotlin
val updated = client.inboxes.update("inbox_abc123") {
    displayName = "New Name"
}
```

`displayName` is required when updating.

## Delete Inbox

```kotlin
client.inboxes.delete("inbox_abc123")
```
