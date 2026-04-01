package com.agentmail4k.dsl

import com.agentmail4k.sdk.AgentMailClient
import com.agentmail4k.sdk.builder.CreateWebhookBuilder
import com.agentmail4k.sdk.builder.ListWebhooksBuilder
import com.agentmail4k.sdk.builder.UpdateWebhookBuilder

/** Lists all webhooks with optional pagination. */
suspend fun AgentMailClient.listWebhooks(block: ListWebhooksBuilder.() -> Unit = {}) =
  webhooks.list(block)

/** Creates a new webhook. */
suspend fun AgentMailClient.createWebhook(block: CreateWebhookBuilder.() -> Unit) =
  webhooks.create(block)

/** Retrieves a webhook by ID. */
suspend fun AgentMailClient.getWebhook(webhookId: String) =
  webhooks.get(webhookId)

/** Updates a webhook by ID. */
suspend fun AgentMailClient.updateWebhook(webhookId: String, block: UpdateWebhookBuilder.() -> Unit) =
  webhooks.update(webhookId, block)

/** Deletes a webhook by ID. */
suspend fun AgentMailClient.deleteWebhook(webhookId: String) =
  webhooks.delete(webhookId)
