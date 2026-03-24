package to.agentmail.sdk.internal

import io.ktor.http.encodeURLPathPart

internal object ApiPaths {
    const val INBOXES = "v0/inboxes"
    const val THREADS = "v0/threads"
    const val DRAFTS = "v0/drafts"
    const val DOMAINS = "v0/domains"
    const val PODS = "v0/pods"
    const val WEBHOOKS = "v0/webhooks"
    const val LISTS = "v0/lists"
    const val METRICS = "v0/metrics"
    const val API_KEYS = "v0/api-keys"
    const val ORGANIZATIONS = "v0/organizations"

    fun inbox(inboxId: String) = "$INBOXES/${inboxId.encodeURLPathPart()}"
    fun pod(podId: String) = "$PODS/${podId.encodeURLPathPart()}"
}
