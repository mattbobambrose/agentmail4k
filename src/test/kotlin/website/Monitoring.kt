@file:Suppress("unused")

package website

import com.agentmail4k.dsl.monitor
import com.agentmail4k.dsl.poll
import com.agentmail4k.dsl.updateMessage
import com.agentmail4k.sdk.AgentMailClient
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

// --8<-- [start:monitor]
suspend fun monitorExample() {
    val client = AgentMailClient()

    val job = client.monitor("inbox-id") {
        pollInterval = 10.seconds

        onMessage { message ->
            println("New message: ${message.subject}")
        }

        onError { error ->
            println("Error: ${error.message}")
        }
    }

    // Monitor runs in the background as a coroutine Job
    delay(5.minutes)
    job.cancel() // Stop monitoring
    client.close()
}
// --8<-- [end:monitor]

// --8<-- [start:monitor-full]
suspend fun monitorFullMessageExample() {
    val client = AgentMailClient()

    val job = client.monitor("inbox-id") {
        pollInterval = 15.seconds
        includeSpam = false
        includeBlocked = false

        // Use onFullMessage to get complete message bodies
        onFullMessage { message ->
            println("From: ${message.from}")
            println("Subject: ${message.subject}")
            println("Body: ${message.text}")
        }

        onError { error ->
            System.err.println("Monitor error: ${error.message}")
        }
    }

    delay(10.minutes)
    job.cancel()
    client.close()
}
// --8<-- [end:monitor-full]

// --8<-- [start:monitor-filter]
suspend fun monitorFilterExample() {
    val client = AgentMailClient()

    // Only process unread messages, then mark them as read
    val job = client.monitor("inbox-id") {
        filterBy { "unread" in it.labels }

        onMessage { message ->
            println("Unread: ${message.subject}")
            client.updateMessage(message) {
                removeLabels("unread")
                addLabels("read")
            }
        }
    }

    delay(5.minutes)
    job.cancel()
    client.close()
}
// --8<-- [end:monitor-filter]

// --8<-- [start:monitor-filter-advanced]
suspend fun monitorFilterAdvancedExample() {
    val client = AgentMailClient()

    // Combine multiple conditions
    val job = client.monitor("inbox-id") {
        filterBy {
            it.from.endsWith("@example.com") && it.attachments.isNotEmpty()
        }

        onFullMessage { message ->
            println("${message.from} sent ${message.attachments.size} attachment(s)")
        }
    }

    delay(5.minutes)
    job.cancel()
    client.close()
}
// --8<-- [end:monitor-filter-advanced]

// --8<-- [start:poll]
suspend fun pollExample() {
    val client = AgentMailClient()

    // poll() is a simplified wrapper around monitor()
    val job = client.poll("inbox-id", interval = 10.seconds) { message ->
        println("${message.subject} — from ${message.from}")
    }

    delay(5.minutes)
    job.cancel()
    client.close()
}
// --8<-- [end:poll]

// --8<-- [start:poll-filter]
suspend fun pollFilterExample() {
    val client = AgentMailClient()

    // Only poll for messages with attachments
    val job = client.poll(
        "inbox-id",
        interval = 10.seconds,
        filter = { it.attachments.isNotEmpty() },
    ) { message ->
        println("${message.subject} has ${message.attachments.size} attachment(s)")
    }

    delay(5.minutes)
    job.cancel()
    client.close()
}
// --8<-- [end:poll-filter]
