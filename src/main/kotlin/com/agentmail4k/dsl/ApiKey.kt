package com.agentmail4k.dsl

import com.agentmail4k.sdk.AgentMailClient
import com.agentmail4k.sdk.builder.CreateApiKeyBuilder
import com.agentmail4k.sdk.builder.ListApiKeysBuilder

/** Lists all API keys with optional pagination. */
suspend fun AgentMailClient.listApiKeys(block: ListApiKeysBuilder.() -> Unit = {}) =
  apiKeys.list(block)

/** Creates a new API key. */
suspend fun AgentMailClient.createApiKey(block: CreateApiKeyBuilder.() -> Unit = {}) =
  apiKeys.create(block)

/** Deletes an API key by ID. */
suspend fun AgentMailClient.deleteApiKey(apiKeyId: String) =
  apiKeys.delete(apiKeyId)
