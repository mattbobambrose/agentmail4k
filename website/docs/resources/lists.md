# Lists

Lists manage allow/block rules for senders, recipients, domains, and subjects. They control which messages are accepted or rejected.

## Concepts

Lists are organized by two dimensions:

**Direction** (`ListDirection`):

| Value | Description |
|---|---|
| `ALLOW` | Permit matching messages |
| `BLOCK` | Reject matching messages |

**Type** (`ListType`):

| Value | Description |
|---|---|
| `SENDER` | Match by sender address |
| `RECIPIENT` | Match by recipient address |
| `DOMAIN` | Match by domain |
| `SUBJECT` | Match by subject |

## List Entries

```kotlin
import com.mattbobambrose.agentmail4k.sdk.model.ListDirection
import com.mattbobambrose.agentmail4k.sdk.model.ListType

val result = client.inboxes("inbox_abc").lists.list(
    direction = ListDirection.BLOCK,
    type = ListType.SENDER,
) {
    limit = 50
}

for (entry in result.entries) {
    println("Blocked sender: ${entry.entry}")
}
```

## Create Entry

```kotlin
client.inboxes("inbox_abc").lists.create(
    direction = ListDirection.BLOCK,
    type = ListType.DOMAIN,
) {
    entry = "spam-domain.com"
}
```

## Get Entry

```kotlin
val entry = client.inboxes("inbox_abc").lists.get(
    direction = ListDirection.ALLOW,
    type = ListType.SENDER,
    entry = "trusted@example.com",
)
```

## Delete Entry

```kotlin
client.inboxes("inbox_abc").lists.delete(
    direction = ListDirection.BLOCK,
    type = ListType.SENDER,
    entry = "spammer@example.com",
)
```

## Top-Level and Scoped Access

Lists are available at multiple levels:

```kotlin
// Organization level
client.lists.list(ListDirection.BLOCK, ListType.SENDER)

// Inbox level
client.inboxes("inbox_abc").lists.list(ListDirection.ALLOW, ListType.DOMAIN)

// Pod level
client.pods("pod_123").lists.list(ListDirection.BLOCK, ListType.SUBJECT)
```
