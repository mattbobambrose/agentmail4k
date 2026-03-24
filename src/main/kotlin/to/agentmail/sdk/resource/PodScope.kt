package to.agentmail.sdk.resource

import io.ktor.client.*
import io.ktor.http.encodeURLPathPart

class PodScope internal constructor(
    private val client: HttpClient,
    private val podId: String,
) {
    private val encodedPodId = podId.encodeURLPathPart()

    val inboxes: InboxResource by lazy {
        InboxResource(client, "v0/pods/$encodedPodId/inboxes")
    }

    val threads: ThreadResource by lazy {
        ThreadResource(client, "v0/pods/$encodedPodId/threads")
    }

    val drafts: DraftResource by lazy {
        DraftResource(client, "v0/pods/$encodedPodId/drafts")
    }

    val domains: DomainResource by lazy {
        DomainResource(client, "v0/pods/$encodedPodId/domains")
    }

    val lists: ListResource by lazy {
        ListResource(client, "v0/pods/$encodedPodId/lists")
    }

    val metrics: MetricsResource by lazy {
        MetricsResource(client, "v0/pods/$encodedPodId/metrics")
    }

    val apiKeys: ApiKeyResource by lazy {
        ApiKeyResource(client, "v0/pods/$encodedPodId/api-keys")
    }

    fun inboxes(inboxId: String): InboxScope = InboxScope(client, inboxId)
}
