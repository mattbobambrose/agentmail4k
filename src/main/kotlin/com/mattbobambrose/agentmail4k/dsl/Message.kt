package com.mattbobambrose.agentmail4k.dsl

import com.mattbobambrose.agentmail4k.sdk.AgentMailClient
import com.mattbobambrose.agentmail4k.sdk.builder.ForwardMessageBuilder
import com.mattbobambrose.agentmail4k.sdk.builder.ListMessagesBuilder
import com.mattbobambrose.agentmail4k.sdk.builder.ReplyAllBuilder
import com.mattbobambrose.agentmail4k.sdk.builder.ReplyBuilder
import com.mattbobambrose.agentmail4k.sdk.builder.SendMessage
import com.mattbobambrose.agentmail4k.sdk.builder.UpdateMessageBuilder
import com.mattbobambrose.agentmail4k.sdk.model.Message
import com.mattbobambrose.agentmail4k.sdk.model.SendMessageResponse

/** Lists messages in an inbox with optional pagination and filtering. */
suspend fun AgentMailClient.listMessages(
  inboxId: String,
  block: ListMessagesBuilder.() -> Unit = {},
) = inboxes(inboxId).messages.list(block)

/** Fetches the full message content for a message (useful after listing which returns previews). */
suspend fun AgentMailClient.toFullMessage(message: Message) =
  inboxes(message.inboxId).messages.get(message.messageId)

/** Sends an email message from an inbox to recipients. */
suspend fun AgentMailClient.sendMessage(block: SendMessage.() -> Unit): SendMessageResponse {
  val sendMessage = SendMessage().apply(block)
  return inboxes(sendMessage.from).messages.send {
    to = sendMessage.to
    cc = sendMessage.cc
    bcc = sendMessage.bcc
    subject = sendMessage.subject
    text = sendMessage.text
    html = sendMessage.html
  }
}

/** Sends a reply to a specific message. */
suspend fun AgentMailClient.replyToMessage(
  message: Message,
  block: ReplyBuilder.() -> Unit,
) = inboxes(message.inboxId).messages.reply(message.messageId, block)

/** Sends a reply-all to a specific message. */
suspend fun AgentMailClient.replyAllToMessage(
  message: Message,
  block: ReplyAllBuilder.() -> Unit,
) = inboxes(message.inboxId).messages.replyAll(message.messageId, block)

/** Forwards a message to new recipients. */
suspend fun AgentMailClient.forwardMessage(
  message: Message,
  block: ForwardMessageBuilder.() -> Unit,
) = inboxes(message.inboxId).messages.forward(message.messageId, block)

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
