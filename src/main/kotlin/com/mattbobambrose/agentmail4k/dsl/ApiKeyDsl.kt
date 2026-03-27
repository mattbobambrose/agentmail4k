package com.mattbobambrose.agentmail4k.dsl

import com.mattbobambrose.agentmail4k.sdk.AgentMailClient
import com.mattbobambrose.agentmail4k.sdk.builder.CreateApiKeyBuilder
import com.mattbobambrose.agentmail4k.sdk.builder.ListApiKeysBuilder

suspend fun AgentMailClient.listApiKeys(block: ListApiKeysBuilder.() -> Unit = {}) =
  apiKeys.list(block)

suspend fun AgentMailClient.createApiKey(block: CreateApiKeyBuilder.() -> Unit = {}) =
  apiKeys.create(block)

suspend fun AgentMailClient.deleteApiKey(apiKeyId: String) =
  apiKeys.delete(apiKeyId)
