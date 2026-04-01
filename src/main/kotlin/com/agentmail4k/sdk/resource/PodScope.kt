package com.agentmail4k.sdk.resource

import io.ktor.client.HttpClient
import com.agentmail4k.sdk.internal.ApiPaths

/** Scoped access to resources nested under a specific pod, including inboxes, threads, drafts, domains, lists, metrics, and API keys. */
class PodScope internal constructor(
  private val client: HttpClient,
  private val podId: String,
) {
  private val podPath = ApiPaths.pod(podId)

  val inboxes: InboxResource by lazy {
    InboxResource(client, "$podPath/inboxes")
  }

  val threads: ThreadResource by lazy {
    ThreadResource(client, "$podPath/threads")
  }

  val drafts: DraftResource by lazy {
    DraftResource(client, "$podPath/drafts")
  }

  val domains: DomainResource by lazy {
    DomainResource(client, "$podPath/domains")
  }

  val lists: ListResource by lazy {
    ListResource(client, "$podPath/lists")
  }

  val metrics: MetricsResource by lazy {
    MetricsResource(client, "$podPath/metrics")
  }

  val apiKeys: ApiKeyResource by lazy {
    ApiKeyResource(client, "$podPath/api-keys")
  }

  fun inboxes(inboxId: String): InboxScope = InboxScope(client, inboxId)
}
