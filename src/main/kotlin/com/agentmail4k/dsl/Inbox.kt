package com.agentmail4k.dsl

import com.agentmail4k.sdk.AgentMailClient
import com.agentmail4k.sdk.builder.ListInboxesBuilder
import com.agentmail4k.sdk.builder.UpdateInboxBuilder

/** Lists all inboxes with optional pagination. */
suspend fun AgentMailClient.listInboxes(block: ListInboxesBuilder.() -> Unit = {}) =
  inboxes.list(block)

/** Creates a new inbox with the given username, domain, and display name. */
suspend fun AgentMailClient.createInbox(username: String, domain: String, displayName: String) =
  inboxes.create {
    this.username = username
    this.domain = domain
    this.displayName = displayName
  }

/** Retrieves an inbox by ID. */
suspend fun AgentMailClient.getInbox(inboxId: String) =
  inboxes.get(inboxId)

/** Updates an inbox by ID. */
suspend fun AgentMailClient.updateInbox(inboxId: String, block: UpdateInboxBuilder.() -> Unit) =
  inboxes.update(inboxId, block)

/** Deletes an inbox by ID. */
suspend fun AgentMailClient.deleteInbox(inboxId: String) =
  inboxes.delete(inboxId)
