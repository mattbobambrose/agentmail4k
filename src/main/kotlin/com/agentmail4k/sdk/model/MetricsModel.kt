package com.agentmail4k.sdk.model

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Response containing a list of aggregated email event metrics. */
@Serializable
data class QueryMetricsResponse(
  val metrics: List<Metric>,
)

/** A single metric data point with event type, count, and timestamp. */
@Serializable
data class Metric(
  @SerialName("event_type") val eventType: String,
  val count: Int,
  val timestamp: Instant,
)

/** Time period for metric aggregation: hour, day, week, or month. */
enum class MetricsPeriod(val value: String) {
  HOUR("hour"),
  DAY("day"),
  WEEK("week"),
  MONTH("month"),
}
