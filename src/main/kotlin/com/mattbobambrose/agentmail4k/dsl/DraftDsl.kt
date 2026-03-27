package com.mattbobambrose.agentmail4k.dsl

import com.mattbobambrose.agentmail4k.sdk.AgentMailClient
import com.mattbobambrose.agentmail4k.sdk.builder.CreateDraftBuilder
import com.mattbobambrose.agentmail4k.sdk.builder.ListDraftsBuilder
import com.mattbobambrose.agentmail4k.sdk.builder.SendDraftBuilder
import com.mattbobambrose.agentmail4k.sdk.builder.UpdateDraftBuilder

suspend fun AgentMailClient.listDrafts(
  inboxId: String,
  block: ListDraftsBuilder.() -> Unit = {},
) = inboxes(inboxId).drafts.list(block)

suspend fun AgentMailClient.createDraft(
  inboxId: String,
  block: CreateDraftBuilder.() -> Unit,
) = inboxes(inboxId).drafts.create(block)

suspend fun AgentMailClient.getDraft(inboxId: String, draftId: String) =
  inboxes(inboxId).drafts.get(draftId)

suspend fun AgentMailClient.updateDraft(
  inboxId: String,
  draftId: String,
  block: UpdateDraftBuilder.() -> Unit,
) = inboxes(inboxId).drafts.update(draftId, block)

suspend fun AgentMailClient.deleteDraft(inboxId: String, draftId: String) =
  inboxes(inboxId).drafts.delete(draftId)

suspend fun AgentMailClient.sendDraft(
  inboxId: String,
  draftId: String,
  block: SendDraftBuilder.() -> Unit = {},
) = inboxes(inboxId).drafts.send(draftId, block)

suspend fun AgentMailClient.getDraftAttachment(
  inboxId: String,
  draftId: String,
  attachmentId: String,
) = inboxes(inboxId).drafts.getAttachment(draftId, attachmentId)
