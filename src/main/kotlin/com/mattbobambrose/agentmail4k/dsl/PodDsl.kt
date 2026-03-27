package com.mattbobambrose.agentmail4k.dsl

import com.mattbobambrose.agentmail4k.sdk.AgentMailClient
import com.mattbobambrose.agentmail4k.sdk.builder.ListPodsBuilder

suspend fun AgentMailClient.listPods(block: ListPodsBuilder.() -> Unit = {}) =
  pods.list(block)

suspend fun AgentMailClient.createPod() =
  pods.create()

suspend fun AgentMailClient.getPod(podId: String) =
  pods.get(podId)

suspend fun AgentMailClient.deletePod(podId: String) =
  pods.delete(podId)
