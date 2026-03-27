package com.mattbobambrose.agentmail4k.dsl

import com.mattbobambrose.agentmail4k.sdk.AgentMailClient
import com.mattbobambrose.agentmail4k.sdk.builder.CreateWebhookBuilder
import com.mattbobambrose.agentmail4k.sdk.builder.ListWebhooksBuilder
import com.mattbobambrose.agentmail4k.sdk.builder.UpdateWebhookBuilder

suspend fun AgentMailClient.listWebhooks(block: ListWebhooksBuilder.() -> Unit = {}) =
  webhooks.list(block)

suspend fun AgentMailClient.createWebhook(block: CreateWebhookBuilder.() -> Unit) =
  webhooks.create(block)

suspend fun AgentMailClient.getWebhook(webhookId: String) =
  webhooks.get(webhookId)

suspend fun AgentMailClient.updateWebhook(webhookId: String, block: UpdateWebhookBuilder.() -> Unit) =
  webhooks.update(webhookId, block)

suspend fun AgentMailClient.deleteWebhook(webhookId: String) =
  webhooks.delete(webhookId)
