@file:Suppress("unused")

package website

import com.agentmail4k.dsl.bulk
import com.agentmail4k.sdk.AgentMailClient

// --8<-- [start:bulk-send]
suspend fun bulkSendExample() {
    val client = AgentMailClient()

    val results = client.bulk {
        send(
            inboxId = "inbox-id",
            recipients = listOf(
                "user1@example.com",
                "user2@example.com",
                "user3@example.com",
            ),
        ) {
            subject = "Monthly Newsletter"
            text = "Here's your monthly update."
            html = "<h1>Monthly Newsletter</h1><p>Here's your monthly update.</p>"
        }
    }

    println("Sent ${results.size} messages")
    for (result in results) {
        println("  Message ID: ${result.messageId}")
    }
    client.close()
}
// --8<-- [end:bulk-send]

// --8<-- [start:bulk-threads]
suspend fun bulkThreadsExample() {
    val client = AgentMailClient()

    client.bulk {
        forEachThread(
            inboxId = "inbox-id",
            filter = { limit = 50 },
        ) { thread ->
            println("Thread: ${thread.subject} (${thread.messageCount} messages)")
        }
    }

    client.close()
}
// --8<-- [end:bulk-threads]
