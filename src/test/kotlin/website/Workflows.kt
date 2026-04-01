@file:Suppress("unused")

package website

import com.mattbobambrose.agentmail4k.dsl.autoReply
import com.mattbobambrose.agentmail4k.dsl.bulk
import com.mattbobambrose.agentmail4k.dsl.monitor
import com.mattbobambrose.agentmail4k.dsl.poll
import com.mattbobambrose.agentmail4k.sdk.AgentMailClient
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

// --8<-- [start:auto-reply]
suspend fun autoReplyExample() {
    val client = AgentMailClient()

    val job = client.autoReply("inbox-id") {
        pollInterval = 10.seconds

        // Match specific messages with rules
        rule(
            match = { it.subject?.contains("pricing", ignoreCase = true) == true },
            reply = {
                text = "Thanks for your interest! Our pricing page is at https://example.com/pricing"
            }
        )

        rule(
            match = { it.subject?.contains("support", ignoreCase = true) == true },
            reply = {
                text = "We've received your support request and will get back to you shortly."
            }
        )

        // Default reply for unmatched messages
        default {
            text = "Thank you for your message. We'll respond soon."
        }
    }

    delay(1.minutes)
    job.cancel()
    client.close()
}
// --8<-- [end:auto-reply]

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
