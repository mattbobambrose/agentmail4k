@file:Suppress("unused")

package website

import com.agentmail4k.dsl.listMessages
import com.agentmail4k.dsl.replyToMessage
import com.agentmail4k.dsl.sendMessage
import com.agentmail4k.sdk.AgentMailClient
import com.agentmail4k.sdk.RateLimitAction
import com.agentmail4k.sdk.RateLimitExceededException
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

// --8<-- [start:per-sender]
suspend fun perSenderExample() {
    val client = AgentMailClient {
        perSenderRateLimiter {
            maxMessages = 10
            window = 60.seconds
            onLimitExceeded = RateLimitAction.STOP
        }
    }

    // Each inbox is tracked independently — "inbox-a" and "inbox-b"
    // each get their own 10-message window
    client.sendMessage {
        from = "inbox-a"
        to = listOf("user@example.com")
        subject = "From inbox A"
        text = "This counts toward inbox-a's limit."
    }

    client.sendMessage {
        from = "inbox-b"
        to = listOf("user@example.com")
        subject = "From inbox B"
        text = "This counts toward inbox-b's limit."
    }

    client.close()
}
// --8<-- [end:per-sender]

// --8<-- [start:per-recipient]
suspend fun perRecipientExample() {
    val client = AgentMailClient {
        perRecipientRateLimiter {
            maxMessages = 3
            window = 30.seconds
            onLimitExceeded = RateLimitAction.SKIP
        }
    }

    // Each recipient address is tracked independently
    val response = client.sendMessage {
        from = "inbox-id"
        to = listOf("alice@example.com", "bob@example.com")
        subject = "Update"
        text = "Both recipients are checked against their own limits."
    }

    if (response == null) {
        println("Message skipped — a recipient hit their rate limit")
    }

    client.close()
}
// --8<-- [end:per-recipient]

// --8<-- [start:delay]
suspend fun delayExample() {
    val client = AgentMailClient {
        perSenderRateLimiter {
            maxMessages = 5
            window = 10.seconds
            onLimitExceeded = RateLimitAction.DELAY
        }
    }

    // Sends 10 messages — the first 5 go immediately,
    // then the coroutine suspends until the window clears
    for (i in 1..10) {
        client.sendMessage {
            from = "inbox-id"
            to = listOf("recipient@example.com")
            subject = "Message #$i"
            text = "Automatically throttled to 5 per 10 seconds."
        }
        println("Sent message #$i")
    }

    client.close()
}
// --8<-- [end:delay]

// --8<-- [start:stop]
suspend fun stopExample() {
    val client = AgentMailClient {
        perSenderRateLimiter {
            maxMessages = 2
            window = 10.seconds
            onLimitExceeded = RateLimitAction.STOP
        }
    }

    try {
        repeat(5) { i ->
            client.sendMessage {
                from = "inbox-id"
                to = listOf("recipient@example.com")
                subject = "Message #${i + 1}"
                text = "This will throw on the 3rd attempt."
            }
        }
    } catch (e: RateLimitExceededException) {
        println("Blocked: ${e.message}")
        // Blocked: Rate limit exceeded for 'inbox-id' (2 per 10s)
    }

    client.close()
}
// --8<-- [end:stop]

// --8<-- [start:combined]
suspend fun combinedExample() {
    val client = AgentMailClient {
        // Limit each inbox to 20 messages per minute
        perSenderRateLimiter {
            maxMessages = 20
            window = 1.minutes
            onLimitExceeded = RateLimitAction.DELAY
        }

        // Limit each recipient to 3 messages per minute
        perRecipientRateLimiter {
            maxMessages = 3
            window = 1.minutes
            onLimitExceeded = RateLimitAction.STOP
        }
    }

    // Both limits are checked: sender limit first, then each recipient
    client.sendMessage {
        from = "inbox-id"
        to = listOf("recipient@example.com")
        subject = "Hello"
        text = "Protected by both sender and recipient limits."
    }

    client.close()
}
// --8<-- [end:combined]

// --8<-- [start:reply-rate-limited]
suspend fun replyRateLimited() {
    val client = AgentMailClient {
        perSenderRateLimiter {
            maxMessages = 5
            window = 30.seconds
            onLimitExceeded = RateLimitAction.SKIP
        }
    }

    val messages = client.listMessages("inbox-id")
    val message = messages.messages.first()

    // Replies and forwards are also rate-limited
    val response = client.replyToMessage(message) {
        text = "Thanks for reaching out!"
    }

    if (response == null) {
        println("Reply skipped — sender rate limit reached")
    }

    client.close()
}
// --8<-- [end:reply-rate-limited]
