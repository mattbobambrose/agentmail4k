package to.agentmail.sdk.resource

import io.ktor.client.*

class InboxScope internal constructor(
    private val client: HttpClient,
    private val inboxId: String,
) {
    val messages: MessageResource by lazy {
        MessageResource(client, "v0/inboxes/$inboxId/messages")
    }

    val threads: ThreadResource by lazy {
        ThreadResource(client, "v0/inboxes/$inboxId/threads")
    }

    val drafts: DraftResource by lazy {
        DraftResource(client, "v0/inboxes/$inboxId/drafts")
    }

    val lists: ListResource by lazy {
        ListResource(client, "v0/inboxes/$inboxId/lists")
    }

    val metrics: MetricsResource by lazy {
        MetricsResource(client, "v0/inboxes/$inboxId/metrics")
    }

    val apiKeys: ApiKeyResource by lazy {
        ApiKeyResource(client, "v0/inboxes/$inboxId/api-keys")
    }
}
