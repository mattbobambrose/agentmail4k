package to.agentmail.sdk.resource

import io.ktor.client.*
import io.ktor.http.encodeURLPathPart

class InboxScope internal constructor(
    private val client: HttpClient,
    private val inboxId: String,
) {
    private val encodedInboxId = inboxId.encodeURLPathPart()

    val messages: MessageResource by lazy {
        MessageResource(client, "v0/inboxes/$encodedInboxId/messages")
    }

    val threads: ThreadResource by lazy {
        ThreadResource(client, "v0/inboxes/$encodedInboxId/threads")
    }

    val drafts: DraftResource by lazy {
        DraftResource(client, "v0/inboxes/$encodedInboxId/drafts")
    }

    val lists: ListResource by lazy {
        ListResource(client, "v0/inboxes/$encodedInboxId/lists")
    }

    val metrics: MetricsResource by lazy {
        MetricsResource(client, "v0/inboxes/$encodedInboxId/metrics")
    }

    val apiKeys: ApiKeyResource by lazy {
        ApiKeyResource(client, "v0/inboxes/$encodedInboxId/api-keys")
    }
}
