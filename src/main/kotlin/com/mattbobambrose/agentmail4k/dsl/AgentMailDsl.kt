package com.mattbobambrose.agentmail4k.dsl

import com.mattbobambrose.agentmail4k.sdk.AgentMailClient
import com.mattbobambrose.agentmail4k.sdk.builder.SendMessage
import com.mattbobambrose.agentmail4k.sdk.model.Message
import com.mattbobambrose.agentmail4k.sdk.model.SendMessageResponse

suspend fun AgentMailClient.createInbox(username: String, domain: String, displayName: String) =
  inboxes.create {
    this.username = username
    this.domain = domain
    this.displayName = displayName
  }

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

suspend fun AgentMailClient.fullMessage(message: Message) =
  inboxes(message.inboxId).messages.get(message.messageId)
