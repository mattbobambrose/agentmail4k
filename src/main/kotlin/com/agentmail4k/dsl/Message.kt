package com.agentmail4k.dsl

import com.agentmail4k.sdk.AgentMailClient
import com.agentmail4k.sdk.builder.ForwardMessageBuilder
import com.agentmail4k.sdk.builder.ListMessagesBuilder
import com.agentmail4k.sdk.builder.ReplyAllBuilder
import com.agentmail4k.sdk.builder.ReplyBuilder
import com.agentmail4k.sdk.builder.SendMessage
import com.agentmail4k.sdk.builder.UpdateMessageBuilder
import com.agentmail4k.sdk.model.Message
import com.agentmail4k.sdk.model.SendMessageResponse

/** Lists messages in an inbox with optional pagination and filtering. */
suspend fun AgentMailClient.listMessages(
  inboxId: String,
  block: ListMessagesBuilder.() -> Unit = {},
) = inboxes(inboxId).messages.list(block)

/** Fetches the full message content for a message (useful after listing which returns previews). */
suspend fun AgentMailClient.toFullMessage(message: Message) =
  inboxes(message.inboxId).messages.get(message.messageId)

/** Sends an email message from an inbox to recipients. Returns `null` if rate-limited with [SKIP][com.agentmail4k.sdk.RateLimitAction.SKIP]. */
suspend fun AgentMailClient.sendMessage(block: SendMessage.() -> Unit): SendMessageResponse? {
  val sendMessage = SendMessage().apply(block)
  if (perSenderRateLimiter?.acquire(sendMessage.from) == false) return null
  perRecipientRateLimiter?.let { limiter ->
    for (recipient in sendMessage.to + (sendMessage.cc ?: emptyList()) + (sendMessage.bcc ?: emptyList())) {
      if (!limiter.acquire(recipient)) return null
    }
  }
  return inboxes(sendMessage.from).messages.send {
    to = sendMessage.to
    cc = sendMessage.cc
    bcc = sendMessage.bcc
    subject = sendMessage.subject
    text = sendMessage.text
    html = sendMessage.html
  }
}

/** Sends a reply to a specific message. Returns `null` if rate-limited with [SKIP][com.agentmail4k.sdk.RateLimitAction.SKIP]. */
suspend fun AgentMailClient.replyToMessage(
  message: Message,
  block: ReplyBuilder.() -> Unit,
): SendMessageResponse? {
  if (perSenderRateLimiter?.acquire(message.inboxId) == false) return null
  if (perRecipientRateLimiter?.acquire(message.from) == false) return null
  return inboxes(message.inboxId).messages.reply(message.messageId, block)
}

/** Sends a reply-all to a specific message. Returns `null` if rate-limited with [SKIP][com.agentmail4k.sdk.RateLimitAction.SKIP]. */
suspend fun AgentMailClient.replyAllToMessage(
  message: Message,
  block: ReplyAllBuilder.() -> Unit,
): SendMessageResponse? {
  if (perSenderRateLimiter?.acquire(message.inboxId) == false) return null
  perRecipientRateLimiter?.let { limiter ->
    for (recipient in listOf(message.from) + message.to + message.cc) {
      if (!limiter.acquire(recipient)) return null
    }
  }
  return inboxes(message.inboxId).messages.replyAll(message.messageId, block)
}

/** Forwards a message to new recipients. Returns `null` if rate-limited with [SKIP][com.agentmail4k.sdk.RateLimitAction.SKIP]. */
suspend fun AgentMailClient.forwardMessage(
  message: Message,
  block: ForwardMessageBuilder.() -> Unit,
): SendMessageResponse? {
  if (perSenderRateLimiter?.acquire(message.inboxId) == false) return null
  perRecipientRateLimiter?.let { limiter ->
    val builder = ForwardMessageBuilder().apply(block)
    for (recipient in builder.to + (builder.cc ?: emptyList()) + (builder.bcc ?: emptyList())) {
      if (!limiter.acquire(recipient)) return null
    }
  }
  return inboxes(message.inboxId).messages.forward(message.messageId, block)
}

/** Updates a message's labels. */
suspend fun AgentMailClient.updateMessage(
  message: Message,
  block: UpdateMessageBuilder.() -> Unit,
) = inboxes(message.inboxId).messages.update(message.messageId, block)

/** Retrieves a message attachment's binary data. */
suspend fun AgentMailClient.getAttachment(
  message: Message,
  attachmentId: String,
) = inboxes(message.inboxId).messages.getAttachment(message.messageId, attachmentId)

/** Retrieves the raw RFC 2822 content of a message. */
suspend fun AgentMailClient.getRawMessage(
  message: Message,
) = inboxes(message.inboxId).messages.getRaw(message.messageId)
