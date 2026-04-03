@file:Suppress("unused")

package website

import com.agentmail4k.dsl.autoReply
import com.agentmail4k.sdk.AgentMailClient
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

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
