package to.agentmail.sdk.workflow

import kotlinx.coroutines.*
import to.agentmail.sdk.AgentMail
import to.agentmail.sdk.AgentMailDsl
import to.agentmail.sdk.model.Message
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@AgentMailDsl
class MonitorBuilder {
    private var onMessage: (suspend (Message) -> Unit)? = null
    private var onError: (suspend (Throwable) -> Unit)? = null
    var pollInterval: Duration = 5.seconds
    var includeSpam: Boolean = false
    var includeBlocked: Boolean = false

    fun onMessage(handler: suspend (Message) -> Unit) {
        onMessage = handler
    }

    fun onError(handler: suspend (Throwable) -> Unit) {
        onError = handler
    }

    internal fun build() = MonitorConfig(
        onMessage = onMessage,
        onError = onError,
        pollInterval = pollInterval,
        includeSpam = includeSpam,
        includeBlocked = includeBlocked,
    )
}

internal data class MonitorConfig(
    val onMessage: (suspend (Message) -> Unit)?,
    val onError: (suspend (Throwable) -> Unit)?,
    val pollInterval: Duration,
    val includeSpam: Boolean,
    val includeBlocked: Boolean,
)

fun AgentMail.monitor(
    inboxId: String,
    scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
    block: MonitorBuilder.() -> Unit,
): Job {
    val config = MonitorBuilder().apply(block).build()
    val client = this

    return scope.launch {
        var lastTimestamp: String? = null

        while (isActive) {
            try {
                val messages = client.inboxes(inboxId).messages.list {
                    ascending = false
                    limit = 50
                    includeSpam = config.includeSpam
                    includeBlocked = config.includeBlocked
                    lastTimestamp?.let { after = it }
                }

                if (messages.messages.isNotEmpty()) {
                    lastTimestamp = messages.messages.first().timestamp.toString()
                    for (message in messages.messages.reversed()) {
                        config.onMessage?.invoke(message)
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                config.onError?.invoke(e)
            }

            delay(config.pollInterval)
        }
    }
}
