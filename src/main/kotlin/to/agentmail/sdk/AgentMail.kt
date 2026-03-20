package to.agentmail.sdk

import io.ktor.client.*
import to.agentmail.sdk.internal.HttpClientFactory
import to.agentmail.sdk.resource.*
import java.io.Closeable

class AgentMail private constructor(
    private val httpClient: HttpClient,
) : Closeable {

    val inboxes: InboxResource = InboxResource(httpClient)
    val threads: ThreadResource = ThreadResource(httpClient, "v0/threads")
    val drafts: DraftResource = DraftResource(httpClient, "v0/drafts")
    val domains: DomainResource = DomainResource(httpClient)
    val pods: PodResource = PodResource(httpClient)
    val webhooks: WebhookResource = WebhookResource(httpClient)
    val lists: ListResource = ListResource(httpClient, "v0/lists")
    val metrics: MetricsResource = MetricsResource(httpClient, "v0/metrics")
    val apiKeys: ApiKeyResource = ApiKeyResource(httpClient, "v0/api-keys")
    val organization: OrganizationResource = OrganizationResource(httpClient)

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
