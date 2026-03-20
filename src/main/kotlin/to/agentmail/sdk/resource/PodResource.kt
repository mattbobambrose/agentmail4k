package to.agentmail.sdk.resource

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import to.agentmail.sdk.builder.ListPodsBuilder
import to.agentmail.sdk.model.Pod
import to.agentmail.sdk.model.PodList

class PodResource internal constructor(
    private val client: HttpClient,
) {
    private val basePath = "v0/pods"

    suspend fun list(block: ListPodsBuilder.() -> Unit = {}): PodList {
        val params = ListPodsBuilder().apply(block).toQueryParams()
        return client.get(basePath) {
            params.forEach { (k, v) -> parameter(k, v) }
        }.body()
    }

    suspend fun create(): Pod {
        return client.post(basePath).body()
    }

    suspend fun get(podId: String): Pod {
        return client.get("$basePath/$podId").body()
    }

    suspend fun delete(podId: String) {
        client.delete("$basePath/$podId")
    }
}
