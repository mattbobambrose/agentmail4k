package to.agentmail.sdk.resource

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import to.agentmail.sdk.builder.CreateInboxBuilder
import to.agentmail.sdk.builder.ListInboxesBuilder
import to.agentmail.sdk.builder.UpdateInboxBuilder
import to.agentmail.sdk.model.Inbox
import to.agentmail.sdk.model.InboxList

class InboxResource internal constructor(
    private val client: HttpClient,
    private val basePath: String = "v0/inboxes",
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
        return client.get("$basePath/$inboxId").body()
    }

    suspend fun update(inboxId: String, block: UpdateInboxBuilder.() -> Unit): Inbox {
        val body = UpdateInboxBuilder().apply(block).build()
        return client.patch("$basePath/$inboxId") {
            setBody(body)
        }.body()
    }

    suspend fun delete(inboxId: String) {
        client.delete("$basePath/$inboxId")
    }
}
