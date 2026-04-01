package com.agentmail4k.dsl

import com.agentmail4k.sdk.AgentMailClient
import com.agentmail4k.sdk.builder.QueryMetricsBuilder

/** Queries email event metrics with optional filters. */
suspend fun AgentMailClient.queryMetrics(block: QueryMetricsBuilder.() -> Unit = {}) =
  metrics.query(block)
