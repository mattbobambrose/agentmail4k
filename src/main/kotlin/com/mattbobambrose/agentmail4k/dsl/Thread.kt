package com.mattbobambrose.agentmail4k.dsl

import com.mattbobambrose.agentmail4k.sdk.AgentMailClient
import com.mattbobambrose.agentmail4k.sdk.builder.DeleteThreadBuilder
import com.mattbobambrose.agentmail4k.sdk.builder.ListThreadsBuilder

/** Lists threads in an inbox with optional pagination and filtering. */
suspend fun AgentMailClient.listThreads(
  inboxId: String,
  block: ListThreadsBuilder.() -> Unit = {},
) = inboxes(inboxId).threads.list(block)

/** Retrieves a thread by inbox and thread ID. */
suspend fun AgentMailClient.getThread(inboxId: String, threadId: String) =
  inboxes(inboxId).threads.get(threadId)

/** Deletes a thread by inbox and thread ID. */
suspend fun AgentMailClient.deleteThread(
  inboxId: String,
  threadId: String,
  block: DeleteThreadBuilder.() -> Unit = {},
) = inboxes(inboxId).threads.delete(threadId, block)

/** Retrieves a thread attachment's binary data. */
suspend fun AgentMailClient.getThreadAttachment(
  inboxId: String,
  threadId: String,
  attachmentId: String,
) = inboxes(inboxId).threads.getAttachment(threadId, attachmentId)
