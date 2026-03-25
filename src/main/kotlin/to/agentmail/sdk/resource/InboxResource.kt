package to.agentmail.sdk.resource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.encodeURLPathPart
import to.agentmail.sdk.builder.CreateInboxBuilder
import to.agentmail.sdk.builder.ListInboxesBuilder
import to.agentmail.sdk.builder.UpdateInboxBuilder
import to.agentmail.sdk.model.Inbox
import to.agentmail.sdk.model.InboxList

class InboxResource internal constructor(
  private val client: HttpClient,
  private val basePath: String,
) {
  suspend fun list(block: ListInboxesBuilder.() -> Unit = {}): InboxList {
    val params = ListInboxesBuilder().apply(block).toQueryParams()
    return client.get(basePath) {
      params.forEach { (k, v) -> parameter(k, v) }
    }.body()
  }

  suspend fun create(block: CreateInboxBuilder.() -> Unit = {}): Inbox {
    val body = CreateInboxBuilder().apply(block).build()
    return client.post(basePath) {
      setBody(body)
    }.body()
  }

  suspend fun get(inboxId: String): Inbox {
    require(inboxId.isNotEmpty()) { "Inbox ID must not be empty." }
    return client.get("$basePath/${inboxId.encodeURLPathPart()}").body()
  }

  suspend fun update(inboxId: String, block: UpdateInboxBuilder.() -> Unit): Inbox {
    require(inboxId.isNotEmpty()) { "Inbox ID must not be empty." }
    val body = UpdateInboxBuilder().apply(block).build()
    return client.patch("$basePath/${inboxId.encodeURLPathPart()}") {
      setBody(body)
    }.body()
  }

  suspend fun delete(inboxId: String) {
    require(inboxId.isNotEmpty()) { "Inbox ID must not be empty." }
    client.delete("$basePath/${inboxId.encodeURLPathPart()}")
  }
}
