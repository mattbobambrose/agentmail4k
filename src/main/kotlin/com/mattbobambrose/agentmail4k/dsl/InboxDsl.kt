package com.mattbobambrose.agentmail4k.dsl

import com.mattbobambrose.agentmail4k.sdk.AgentMailClient
import com.mattbobambrose.agentmail4k.sdk.builder.ListInboxesBuilder
import com.mattbobambrose.agentmail4k.sdk.builder.UpdateInboxBuilder

suspend fun AgentMailClient.listInboxes(block: ListInboxesBuilder.() -> Unit = {}) =
  inboxes.list(block)

suspend fun AgentMailClient.createInbox(username: String, domain: String, displayName: String) =
  inboxes.create {
    this.username = username
    this.domain = domain
    this.displayName = displayName
  }

suspend fun AgentMailClient.getInbox(inboxId: String) =
  inboxes.get(inboxId)

suspend fun AgentMailClient.updateInbox(inboxId: String, block: UpdateInboxBuilder.() -> Unit) =
  inboxes.update(inboxId, block)

suspend fun AgentMailClient.deleteInbox(inboxId: String) =
  inboxes.delete(inboxId)
