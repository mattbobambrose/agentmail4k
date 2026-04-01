package com.agentmail4k.sdk.resource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import com.agentmail4k.sdk.builder.QueryMetricsBuilder
import com.agentmail4k.sdk.model.QueryMetricsResponse

/** Provides operations for querying email event metrics. */
class MetricsResource internal constructor(
  private val client: HttpClient,
  private val basePath: String,
) {
  /** Queries email event metrics with optional filters and aggregation. */
  suspend fun query(block: QueryMetricsBuilder.() -> Unit = {}): QueryMetricsResponse {
    val params = QueryMetricsBuilder().apply(block).toQueryParams()
    return client.get(basePath) {
      params.forEach { (k, v) -> parameter(k, v) }
    }.body()
  }
}
