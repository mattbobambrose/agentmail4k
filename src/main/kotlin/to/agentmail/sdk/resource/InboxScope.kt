package to.agentmail.sdk.resource

import io.ktor.client.HttpClient
import to.agentmail.sdk.internal.ApiPaths

class InboxScope internal constructor(
  private val client: HttpClient,
  private val inboxId: String,
) {
  private val inboxPath = ApiPaths.inbox(inboxId)

  val messages: MessageResource by lazy {
    MessageResource(client, "$inboxPath/messages")
  }

  val threads: ThreadResource by lazy {
    ThreadResource(client, "$inboxPath/threads")
  }

  val drafts: DraftResource by lazy {
    DraftResource(client, "$inboxPath/drafts")
  }

  val lists: ListResource by lazy {
    ListResource(client, "$inboxPath/lists")
  }

  val metrics: MetricsResource by lazy {
    MetricsResource(client, "$inboxPath/metrics")
  }

  val apiKeys: ApiKeyResource by lazy {
    ApiKeyResource(client, "$inboxPath/api-keys")
  }
}
