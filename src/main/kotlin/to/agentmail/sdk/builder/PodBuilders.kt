package to.agentmail.sdk.builder

import to.agentmail.sdk.AgentMailDsl

@AgentMailDsl
class CreatePodBuilder {
    internal fun build() = Unit
}

@AgentMailDsl
class ListPodsBuilder {
    var limit: Int? = null
    var pageToken: String? = null
    var ascending: Boolean? = null

    internal fun toQueryParams(): Map<String, String> = buildMap {
        limit?.let { put("limit", it.toString()) }
        pageToken?.let { put("page_token", it) }
        ascending?.let { put("ascending", it.toString()) }
    }
}
