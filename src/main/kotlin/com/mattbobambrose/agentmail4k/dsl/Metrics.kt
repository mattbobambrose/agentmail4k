package com.mattbobambrose.agentmail4k.dsl

import com.mattbobambrose.agentmail4k.sdk.AgentMailClient
import com.mattbobambrose.agentmail4k.sdk.builder.QueryMetricsBuilder

suspend fun AgentMailClient.queryMetrics(block: QueryMetricsBuilder.() -> Unit = {}) =
  metrics.query(block)
