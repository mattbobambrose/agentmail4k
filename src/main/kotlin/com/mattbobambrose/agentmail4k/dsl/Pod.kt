package com.mattbobambrose.agentmail4k.dsl

import com.mattbobambrose.agentmail4k.sdk.AgentMailClient
import com.mattbobambrose.agentmail4k.sdk.builder.ListPodsBuilder

/** Lists all pods with optional pagination. */
suspend fun AgentMailClient.listPods(block: ListPodsBuilder.() -> Unit = {}) =
  pods.list(block)

/** Creates a new pod. */
suspend fun AgentMailClient.createPod() =
  pods.create()

/** Retrieves a pod by ID. */
suspend fun AgentMailClient.getPod(podId: String) =
  pods.get(podId)

/** Deletes a pod by ID. */
suspend fun AgentMailClient.deletePod(podId: String) =
  pods.delete(podId)
