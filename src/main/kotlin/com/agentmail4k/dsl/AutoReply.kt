package com.agentmail4k.dsl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import com.agentmail4k.sdk.AgentMailClient
import com.agentmail4k.sdk.AgentMailDsl
import com.agentmail4k.sdk.builder.ReplyBuilder
import com.agentmail4k.sdk.model.Message

/** DSL builder for configuring automatic reply rules with match conditions and reply actions. */
@AgentMailDsl
class AutoReplyBuilder {
  private val rules = mutableListOf<AutoReplyRule>()
  private var defaultReply: (ReplyBuilder.(Message) -> Unit)? = null
  var pollInterval: kotlin.time.Duration = kotlin.time.Duration.parse("5s")

  /** Adds an auto-reply rule that replies when the [match] predicate returns true. */
  fun rule(
    match: (Message) -> Boolean,
    reply: ReplyBuilder.(Message) -> Unit,
  ) {
    rules.add(AutoReplyRule(match, reply))
  }

  /** Sets the default reply action for messages that don't match any rule. */
  fun default(reply: ReplyBuilder.(Message) -> Unit) {
    defaultReply = reply
  }

  internal fun build() = AutoReplyConfig(rules.toList(), defaultReply, pollInterval)
}

/** A single auto-reply rule pairing a match predicate with a reply builder action. */
internal data class AutoReplyRule(
  val match: (Message) -> Boolean,
  val reply: ReplyBuilder.(Message) -> Unit,
)

/** Immutable configuration for the auto-reply system including rules, default reply, and poll interval. */
internal data class AutoReplyConfig(
  val rules: List<AutoReplyRule>,
  val defaultReply: (ReplyBuilder.(Message) -> Unit)?,
  val pollInterval: kotlin.time.Duration,
)

/** Starts an auto-reply monitor on the given inbox that automatically replies to incoming messages based on configured rules. */
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
