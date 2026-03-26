package com.mattbobambrose.agentmail4k.sdk.model

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QueryMetricsResponse(
  val metrics: List<Metric>,
)

@Serializable
data class Metric(
  @SerialName("event_type") val eventType: String,
  val count: Int,
  val timestamp: Instant,
)

enum class MetricsPeriod(val value: String) {
  HOUR("hour"),
  DAY("day"),
  WEEK("week"),
  MONTH("month"),
}
