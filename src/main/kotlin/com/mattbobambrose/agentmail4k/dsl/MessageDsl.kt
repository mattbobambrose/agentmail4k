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

suspend fun AgentMailClient.listMessages(
  inboxId: String,
  block: ListMessagesBuilder.() -> Unit = {},
) = inboxes(inboxId).messages.list(block)

suspend fun AgentMailClient.toFullMessage(message: Message) =
  inboxes(message.inboxId).messages.get(message.messageId)

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

suspend fun AgentMailClient.replyToMessage(
  message: Message,
  block: ReplyBuilder.() -> Unit,
) = inboxes(message.inboxId).messages.reply(message.messageId, block)

suspend fun AgentMailClient.replyAllToMessage(
  message: Message,
  block: ReplyAllBuilder.() -> Unit,
) = inboxes(message.inboxId).messages.replyAll(message.messageId, block)

suspend fun AgentMailClient.forwardMessage(
  message: Message,
  block: ForwardMessageBuilder.() -> Unit,
) = inboxes(message.inboxId).messages.forward(message.messageId, block)

suspend fun AgentMailClient.updateMessage(
  message: Message,
  block: UpdateMessageBuilder.() -> Unit,
) = inboxes(message.inboxId).messages.update(message.messageId, block)

suspend fun AgentMailClient.getAttachment(
  message: Message,
  attachmentId: String,
) = inboxes(message.inboxId).messages.getAttachment(message.messageId, attachmentId)

suspend fun AgentMailClient.getRawMessage(
  message: Message,
) = inboxes(message.inboxId).messages.getRaw(message.messageId)
