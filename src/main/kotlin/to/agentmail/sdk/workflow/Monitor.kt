package to.agentmail.sdk.workflow

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import to.agentmail.sdk.AgentMail
import to.agentmail.sdk.AgentMailDsl
import to.agentmail.sdk.model.Message
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@AgentMailDsl
class MonitorBuilder {
  private var onMessage: (suspend (Message) -> Unit)? = null
  private var onError: (suspend (Throwable) -> Unit)? = null
  private var fullMessage: Boolean = false
  var pollInterval: Duration = 5.seconds
  var includeSpam: Boolean = false
  var includeBlocked: Boolean = false

  fun onMessage(fullMessage: Boolean = false, handler: suspend (Message) -> Unit) {
    this.fullMessage = fullMessage
    onMessage = handler
  }

  fun onError(handler: suspend (Throwable) -> Unit) {
    onError = handler
  }

  internal fun build() = MonitorConfig(
    onMessage = onMessage,
    onError = onError,
    fullMessage = fullMessage,
    pollInterval = pollInterval,
    includeSpam = includeSpam,
    includeBlocked = includeBlocked,
  )
}

internal data class MonitorConfig(
  val onMessage: (suspend (Message) -> Unit)?,
  val onError: (suspend (Throwable) -> Unit)?,
  val fullMessage: Boolean,
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
            val msg = if (config.fullMessage) message.fullMessage(client) else message
            config.onMessage?.invoke(msg)
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
