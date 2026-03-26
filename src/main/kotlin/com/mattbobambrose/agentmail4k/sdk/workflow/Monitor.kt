package com.mattbobambrose.agentmail4k.sdk.workflow

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import com.mattbobambrose.agentmail4k.dsl.fullMessage
import com.mattbobambrose.agentmail4k.sdk.AgentMailClient
import com.mattbobambrose.agentmail4k.sdk.AgentMailDsl
import com.mattbobambrose.agentmail4k.sdk.model.Message
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

  fun onMessage(handler: suspend (Message) -> Unit) {
    fullMessage = false
    onMessage = handler
  }

  fun onFullMessage(handler: suspend (Message) -> Unit) {
    fullMessage = true
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

fun AgentMailClient.monitor(
  inboxId: String,
  scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
  block: MonitorBuilder.() -> Unit,
): Job {
  val config = MonitorBuilder().apply(block).build()
  val client = this

  return scope.launch {
    val inboxScope = client.inboxes(inboxId)
    var lastTimestamp: String? = null

    while (isActive) {
      try {
        val messages = inboxScope.messages.list {
          ascending = false
          limit = 50
          includeSpam = config.includeSpam
          includeBlocked = config.includeBlocked
          lastTimestamp?.let { after = it }
        }

        if (messages.messages.isNotEmpty()) {
          lastTimestamp = messages.messages.first().timestamp.toString()
          for (message in messages.messages.asReversed()) {
            val msg = if (config.fullMessage) client.fullMessage(message) else message
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
