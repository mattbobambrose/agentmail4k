package to.agentmail.sdk

import io.ktor.client.HttpClient
import to.agentmail.sdk.internal.ApiPaths
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

class AgentMailClient private constructor(
  private val httpClient: HttpClient,
) : Closeable {

  val inboxes: InboxResource = InboxResource(httpClient, ApiPaths.INBOXES)
  val threads: ThreadResource = ThreadResource(httpClient, ApiPaths.THREADS)
  val drafts: DraftResource = DraftResource(httpClient, ApiPaths.DRAFTS)
  val domains: DomainResource = DomainResource(httpClient, ApiPaths.DOMAINS)
  val pods: PodResource = PodResource(httpClient, ApiPaths.PODS)
  val webhooks: WebhookResource = WebhookResource(httpClient, ApiPaths.WEBHOOKS)
  val lists: ListResource = ListResource(httpClient, ApiPaths.LISTS)
  val metrics: MetricsResource = MetricsResource(httpClient, ApiPaths.METRICS)
  val apiKeys: ApiKeyResource = ApiKeyResource(httpClient, ApiPaths.API_KEYS)
  val organization: OrganizationResource = OrganizationResource(httpClient, ApiPaths.ORGANIZATIONS)

  fun inboxes(inboxId: String): InboxScope = InboxScope(httpClient, inboxId)
  fun pods(podId: String): PodScope = PodScope(httpClient, podId)

  override fun close() {
    httpClient.close()
  }

  companion object {
    operator fun invoke(block: AgentMailConfigBuilder.() -> Unit = {}): AgentMailClient {
      val config = AgentMailConfigBuilder().apply(block).build()
      val httpClient = HttpClientFactory.create(config)
      return AgentMailClient(httpClient)
    }
  }
}
