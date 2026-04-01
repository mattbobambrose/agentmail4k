package com.mattbobambrose.agentmail4k.dsl

import com.mattbobambrose.agentmail4k.sdk.AgentMailClient
import com.mattbobambrose.agentmail4k.sdk.builder.CreateDraftBuilder
import com.mattbobambrose.agentmail4k.sdk.builder.ListDraftsBuilder
import com.mattbobambrose.agentmail4k.sdk.builder.SendDraftBuilder
import com.mattbobambrose.agentmail4k.sdk.builder.UpdateDraftBuilder

/** Lists drafts in an inbox with optional pagination and filtering. */
suspend fun AgentMailClient.listDrafts(
  inboxId: String,
  block: ListDraftsBuilder.() -> Unit = {},
) = inboxes(inboxId).drafts.list(block)

/** Creates a new draft in an inbox. */
suspend fun AgentMailClient.createDraft(
  inboxId: String,
  block: CreateDraftBuilder.() -> Unit,
) = inboxes(inboxId).drafts.create(block)

/** Retrieves a draft by inbox and draft ID. */
suspend fun AgentMailClient.getDraft(inboxId: String, draftId: String) =
  inboxes(inboxId).drafts.get(draftId)

/** Updates a draft by inbox and draft ID. */
suspend fun AgentMailClient.updateDraft(
  inboxId: String,
  draftId: String,
  block: UpdateDraftBuilder.() -> Unit,
) = inboxes(inboxId).drafts.update(draftId, block)

/** Deletes a draft by inbox and draft ID. */
suspend fun AgentMailClient.deleteDraft(inboxId: String, draftId: String) =
  inboxes(inboxId).drafts.delete(draftId)

/** Sends a draft as an email message. */
suspend fun AgentMailClient.sendDraft(
  inboxId: String,
  draftId: String,
  block: SendDraftBuilder.() -> Unit = {},
) = inboxes(inboxId).drafts.send(draftId, block)

/** Retrieves a draft attachment's binary data. */
suspend fun AgentMailClient.getDraftAttachment(
  inboxId: String,
  draftId: String,
  attachmentId: String,
) = inboxes(inboxId).drafts.getAttachment(draftId, attachmentId)
