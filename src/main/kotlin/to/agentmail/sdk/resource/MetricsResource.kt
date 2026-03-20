package to.agentmail.sdk.resource

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import to.agentmail.sdk.builder.QueryMetricsBuilder
import to.agentmail.sdk.model.QueryMetricsResponse

class MetricsResource internal constructor(
    private val client: HttpClient,
    private val basePath: String,
) {
    suspend fun query(block: QueryMetricsBuilder.() -> Unit = {}): QueryMetricsResponse {
        val params = QueryMetricsBuilder().apply(block).toQueryParams()
        return client.get(basePath) {
            params.forEach { (k, v) -> parameter(k, v) }
        }.body()
    }
}
