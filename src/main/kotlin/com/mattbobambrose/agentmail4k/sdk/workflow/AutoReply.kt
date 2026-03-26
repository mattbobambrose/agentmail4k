package com.mattbobambrose.agentmail4k.sdk.workflow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import com.mattbobambrose.agentmail4k.sdk.AgentMailClient
import com.mattbobambrose.agentmail4k.sdk.AgentMailDsl
import com.mattbobambrose.agentmail4k.sdk.builder.ReplyBuilder
import com.mattbobambrose.agentmail4k.sdk.model.Message

@AgentMailDsl
class AutoReplyBuilder {
  private val rules = mutableListOf<AutoReplyRule>()
  private var defaultReply: (ReplyBuilder.(Message) -> Unit)? = null
  var pollInterval: kotlin.time.Duration = kotlin.time.Duration.parse("5s")

  fun rule(
    match: (Message) -> Boolean,
    reply: ReplyBuilder.(Message) -> Unit,
  ) {
    rules.add(AutoReplyRule(match, reply))
  }

  fun default(reply: ReplyBuilder.(Message) -> Unit) {
    defaultReply = reply
  }

  internal fun build() = AutoReplyConfig(rules.toList(), defaultReply, pollInterval)
}

internal data class AutoReplyRule(
  val match: (Message) -> Boolean,
  val reply: ReplyBuilder.(Message) -> Unit,
)

internal data class AutoReplyConfig(
  val rules: List<AutoReplyRule>,
  val defaultReply: (ReplyBuilder.(Message) -> Unit)?,
  val pollInterval: kotlin.time.Duration,
)

fun AgentMailClient.autoReply(
  inboxId: String,
  scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
  block: AutoReplyBuilder.() -> Unit,
): Job {
  val config = AutoReplyBuilder().apply(block).build()
  val client = this

  return monitor(inboxId, scope) {
    pollInterval = config.pollInterval

    onMessage { message ->
      val matchedRule = config.rules.firstOrNull { it.match(message) }
      val replyAction = matchedRule?.reply ?: config.defaultReply ?: return@onMessage

      client.inboxes(inboxId).messages.reply(message.messageId) {
        replyAction(this, message)
      }
    }
  }
}
