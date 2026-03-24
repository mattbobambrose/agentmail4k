package to.agentmail.sdk

import io.ktor.client.HttpClient
import to.agentmail.sdk.internal.HttpClientFactory
import to.agentmail.sdk.resource.ApiKeyResource
import to.agentmail.sdk.resource.DomainResource
import to.agentmail.sdk.resource.DraftResource
import to.agentmail.sdk.resource.InboxResource
import to.agentmail.sdk.resource.InboxScope
import to.agentmail.sdk.resource.ListResource
import to.agentmail.sdk.resource.MetricsResource
import to.agentmail.sdk.resource.OrganizationResource
import to.agentmail.sdk.resource.PodResource
import to.agentmail.sdk.resource.PodScope
import to.agentmail.sdk.resource.ThreadResource
import to.agentmail.sdk.resource.WebhookResource
import java.io.Closeable

class AgentMail private constructor(
  private val httpClient: HttpClient,
) : Closeable {

  val inboxes: InboxResource = InboxResource(httpClient, "v0/inboxes")
  val threads: ThreadResource = ThreadResource(httpClient, "v0/threads")
  val drafts: DraftResource = DraftResource(httpClient, "v0/drafts")
  val domains: DomainResource = DomainResource(httpClient, "v0/domains")
  val pods: PodResource = PodResource(httpClient, "v0/pods")
  val webhooks: WebhookResource = WebhookResource(httpClient, "v0/webhooks")
  val lists: ListResource = ListResource(httpClient, "v0/lists")
  val metrics: MetricsResource = MetricsResource(httpClient, "v0/metrics")
  val apiKeys: ApiKeyResource = ApiKeyResource(httpClient, "v0/api-keys")
  val organization: OrganizationResource = OrganizationResource(httpClient, "v0/organizations")

  fun inboxes(inboxId: String): InboxScope = InboxScope(httpClient, inboxId)
  fun pods(podId: String): PodScope = PodScope(httpClient, podId)

  override fun close() {
    httpClient.close()
  }

  companion object {
    operator fun invoke(block: AgentMailConfigBuilder.() -> Unit = {}): AgentMail {
      val config = AgentMailConfigBuilder().apply(block).build()
      val httpClient = HttpClientFactory.create(config)
      return AgentMail(httpClient)
    }
  }
}
