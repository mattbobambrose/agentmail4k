package to.agentmail.dsl

import to.agentmail.sdk.AgentMailClient
import to.agentmail.sdk.builder.SendMessage
import to.agentmail.sdk.model.Message
import to.agentmail.sdk.model.SendMessageResponse

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
