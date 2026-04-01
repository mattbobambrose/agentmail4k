@file:Suppress("unused")

package website

import com.agentmail4k.dsl.getOrganization
import com.agentmail4k.dsl.queryMetrics
import com.agentmail4k.sdk.AgentMailClient
import com.agentmail4k.sdk.model.MetricsPeriod

// --8<-- [start:query-metrics]
suspend fun queryMetricsExample() {
    val client = AgentMailClient()
    val result = client.queryMetrics {
        period = MetricsPeriod.DAY
    }
    for (metric in result.metrics) {
        println("${metric.eventType}: ${metric.count} at ${metric.timestamp}")
    }
    client.close()
}
// --8<-- [end:query-metrics]

// --8<-- [start:metrics-period]
suspend fun metricsPeriodExample() {
    val client = AgentMailClient()

    val hourly = client.queryMetrics { period = MetricsPeriod.HOUR }
    val daily = client.queryMetrics { period = MetricsPeriod.DAY }
    val weekly = client.queryMetrics { period = MetricsPeriod.WEEK }
    val monthly = client.queryMetrics { period = MetricsPeriod.MONTH }

    println("Hourly metrics: ${hourly.metrics.size}")
    println("Daily metrics: ${daily.metrics.size}")
    println("Weekly metrics: ${weekly.metrics.size}")
    println("Monthly metrics: ${monthly.metrics.size}")

    client.close()
}
// --8<-- [end:metrics-period]

// --8<-- [start:get-organization]
suspend fun getOrganizationExample() {
    val client = AgentMailClient()
    val org = client.getOrganization()
    println("Organization: $org")
    client.close()
}
// --8<-- [end:get-organization]
