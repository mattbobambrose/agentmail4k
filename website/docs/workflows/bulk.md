# Bulk Operations

The `bulk` extension function lets you batch multiple operations and execute them sequentially.

## Bulk Send

Send the same message to a list of recipients (one message per recipient):

```kotlin
import to.agentmail.sdk.workflow.bulk

val results = client.bulk {
    send(
        inboxId = "inbox_abc",
        recipients = listOf(
            "user1@example.com",
            "user2@example.com",
            "user3@example.com",
        ),
    ) {
        subject = "Monthly Newsletter"
        text = "Here's what happened this month..."
        html = "<h1>Monthly Newsletter</h1><p>Here's what happened this month...</p>"
    }
}

println("Sent ${results.size} messages")
for (result in results) {
    println("  Message ${result.messageId} in thread ${result.threadId}")
}
```

## Iterate Threads

Process all threads in an inbox with automatic pagination:

```kotlin
client.bulk {
    forEachThread(
        inboxId = "inbox_abc",
        filter = { labels = listOf("unread") },
    ) { thread ->
        println("Thread: ${thread.subject} (${thread.messageCount} messages)")
    }
}
```

## Combining Operations

You can combine multiple operations in a single `bulk` block:

```kotlin
val results = client.bulk {
    // Send to one group
    send("inbox_abc", listOf("team@example.com")) {
        subject = "Team Update"
        text = "Project status update..."
    }

    // Process threads
    forEachThread("inbox_abc") { thread ->
        if (thread.messageCount > 10) {
            println("Long thread: ${thread.subject}")
        }
    }
}
```

Operations execute sequentially in the order they are declared. The returned list contains `SendMessageResponse` objects from all `send` operations.
