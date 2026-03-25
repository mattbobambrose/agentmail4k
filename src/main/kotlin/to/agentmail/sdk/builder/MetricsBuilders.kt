package to.agentmail.sdk.builder

import to.agentmail.sdk.AgentMailDsl
import to.agentmail.sdk.model.MetricsPeriod

@AgentMailDsl
class QueryMetricsBuilder {
  var eventTypes: String? = null
  var start: String? = null
  var end: String? = null
  var period: MetricsPeriod? = null
  var limit: Int? = null
  var descending: Boolean? = null

  internal fun toQueryParams(): Map<String, String> = buildMap {
    eventTypes?.let { put("event_types", it) }
    start?.let { put("start", it) }
    end?.let { put("end", it) }
    period?.let { put("period", it.value) }
    limit?.let { put("limit", it.toString()) }
    descending?.let { put("descending", it.toString()) }
  }
}
