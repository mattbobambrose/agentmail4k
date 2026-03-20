package to.agentmail.sdk.resource

import io.ktor.client.*

class PodScope internal constructor(
    private val client: HttpClient,
    private val podId: String,
) {
    val inboxes: InboxResource by lazy {
        InboxResource(client, "v0/pods/$podId/inboxes")
    }

    val threads: ThreadResource by lazy {
        ThreadResource(client, "v0/pods/$podId/threads")
    }

    val drafts: DraftResource by lazy {
        DraftResource(client, "v0/pods/$podId/drafts")
    }

    val domains: DomainResource by lazy {
        DomainResource(client, "v0/pods/$podId/domains")
    }

    val lists: ListResource by lazy {
        ListResource(client, "v0/pods/$podId/lists")
    }

    val metrics: MetricsResource by lazy {
        MetricsResource(client, "v0/pods/$podId/metrics")
    }

    val apiKeys: ApiKeyResource by lazy {
        ApiKeyResource(client, "v0/pods/$podId/api-keys")
    }

    fun inboxes(inboxId: String): InboxScope = InboxScope(client, inboxId)
}
