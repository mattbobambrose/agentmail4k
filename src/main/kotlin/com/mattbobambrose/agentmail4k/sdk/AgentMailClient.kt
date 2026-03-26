package com.mattbobambrose.agentmail4k.sdk

import io.ktor.client.HttpClient
import com.mattbobambrose.agentmail4k.sdk.internal.ApiPaths
import com.mattbobambrose.agentmail4k.sdk.internal.HttpClientFactory
import com.mattbobambrose.agentmail4k.sdk.resource.ApiKeyResource
import com.mattbobambrose.agentmail4k.sdk.resource.DomainResource
import com.mattbobambrose.agentmail4k.sdk.resource.DraftResource
import com.mattbobambrose.agentmail4k.sdk.resource.InboxResource
import com.mattbobambrose.agentmail4k.sdk.resource.InboxScope
import com.mattbobambrose.agentmail4k.sdk.resource.ListResource
import com.mattbobambrose.agentmail4k.sdk.resource.MetricsResource
import com.mattbobambrose.agentmail4k.sdk.resource.OrganizationResource
import com.mattbobambrose.agentmail4k.sdk.resource.PodResource
import com.mattbobambrose.agentmail4k.sdk.resource.PodScope
import com.mattbobambrose.agentmail4k.sdk.resource.ThreadResource
import com.mattbobambrose.agentmail4k.sdk.resource.WebhookResource
import java.io.Closeable

class AgentMailClient internal constructor(
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
